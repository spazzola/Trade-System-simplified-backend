package tradesystemsimplified.buyer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class BuyerService {

    private InvoiceDao invoiceDao;
    private BuyerDao buyerDao;
    private PriceDao priceDao;
    private OrderService orderService;
    private OrderDao orderDao;

    public BuyerService(InvoiceDao invoiceDao, BuyerDao buyerDao,
                        PriceDao priceDao, OrderService orderService, OrderDao orderDao) {
        this.invoiceDao = invoiceDao;
        this.buyerDao = buyerDao;
        this.priceDao = priceDao;
        this.orderService = orderService;
        this.orderDao = orderDao;
    }


    @Transactional
    public Buyer createBuyer(BuyerDto buyerDto) {
        if (validateBuyer(buyerDto)) {
            Buyer buyer = Buyer.builder()
                    .name(buyerDto.getName())
                    .build();

            return buyerDao.save(buyer);
        }
        throw new RuntimeException("Nie można stworzyć kupca");
    }

    @Transactional
    public List<Buyer> calculateAndSetBalances() {
        List<Buyer> buyers = buyerDao.findAll();

        for (Buyer buyer : buyers) {
            BigDecimal currentBalance = calculateCurrentBalance(buyer);
            buyer.setCurrentBalance(currentBalance);
        }

        return buyers;
    }

    @Transactional
    public List<Buyer> getAll() {
        return buyerDao.findAll();
    }

    @Transactional
    public List<Price> getBuyerProducts(Long id) {
        return priceDao.getBuyerProducts(id);
    }

    @Transactional
    public List<Buyer> getAllWithAverageEarning(int month, int year) {

        Set<Buyer> buyers = new HashSet<>();
        Set<Order> orders = orderService.getMonthOrders(month, year);

        for (Order order : orders) {
            buyers.add(order.getBuyer());
        }

        List<Buyer> resultList = new ArrayList<>();

        for (Buyer buyer : buyers) {
            List<Order> buyerOrders = orderService.getBuyerMonthOrders(buyer.getId(), month, year);

            BigDecimal totalBuyerSum = BigDecimal.valueOf(0);
            BigDecimal totalSupplierSum = BigDecimal.valueOf(0);
            BigDecimal totalQuantity = BigDecimal.valueOf(0);

            for (Order order : buyerOrders) {

                totalBuyerSum = totalBuyerSum.add(order.getOrderDetails().get(0).getBuyerSum());
                totalSupplierSum = totalSupplierSum.add(order.getOrderDetails().get(0).getSupplierSum());
                totalQuantity = totalQuantity.add(order.getOrderDetails().get(0).getQuantity());
            }
            BigDecimal difference = totalBuyerSum.subtract(totalSupplierSum);
            BigDecimal profitPerM3 = difference.divide(totalQuantity, RoundingMode.HALF_EVEN);

            buyer.setAverageProfitPerM3(profitPerM3);
            resultList.add(buyer);
        }
        return resultList;
    }

    @Transactional
    public Buyer updateBuyerName(String oldBuyerName, String newBuyerName) {
        Buyer buyer = buyerDao.findByName(oldBuyerName);
        buyer.setName(newBuyerName);

        return buyerDao.save(buyer);
    }

    @Transactional
    public List<Buyer> getBuyersMonthTakenQuantity(int month, int year) {
        List<Buyer> buyers = buyerDao.findAll();

        for (Buyer buyer : buyers) {
            List<Order> orders = orderDao.getBuyerMonthOrders(buyer.getId(), month, year);
            BigDecimal sumQuantity = BigDecimal.valueOf(0);

            for (Order order : orders) {
                sumQuantity = sumQuantity.add(order.getOrderDetails().get(0).getQuantity());
            }

            buyer.setMonthTakenQuantity(sumQuantity);
        }
        return buyers;
    }

    private BigDecimal calculateCurrentBalance(Buyer buyer) {
        List<Invoice> notUsedInvoices = invoiceDao.getBuyerNotUsedInvoices(buyer.getId());

        BigDecimal balance = BigDecimal.valueOf(0);

        for (Invoice invoice : notUsedInvoices) {
            if (invoice.isCreatedToOrder()) {
                balance = balance.subtract(invoice.getValue());
            }
            balance = balance.add(invoice.getAmountToUse());
        }

        Optional<Invoice> negativeInvoice = invoiceDao.getBuyerNegativeInvoice(buyer.getId());
        if (negativeInvoice.isPresent()) {
            balance = balance.add(negativeInvoice.get().getAmountToUse());
        }

        Optional<List<Invoice>> notPaidInvoices = invoiceDao.getBuyerNotPaidInvoices(buyer.getId());
        if (notPaidInvoices.isPresent()) {
            for (Invoice invoice : notPaidInvoices.get()) {
                balance = balance.subtract(invoice.getValue());
            }
        }
        return balance;
    }

    private boolean validateBuyer(BuyerDto buyerDto) {
        if (buyerDto.getName() == null || buyerDto.getName().equals("")) {
            return false;
        }
        Buyer buyer = buyerDao.findByName(buyerDto.getName());

        return buyer == null;
    }

}
