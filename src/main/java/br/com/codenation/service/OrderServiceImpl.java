package br.com.codenation.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.codenation.model.OrderItem;
import br.com.codenation.model.Product;
import br.com.codenation.repository.ProductRepository;
import br.com.codenation.repository.ProductRepositoryImpl;

public class OrderServiceImpl implements OrderService {
    private ProductRepository productRepository = new ProductRepositoryImpl();


    @Override
    public Double calculateOrderValue(List<OrderItem> items) {
        Double comDesconto = items.stream().filter(i -> productRepository.findById(i.getProductId()).get().getIsSale())
                .mapToDouble(i -> i.getQuantity() * productRepository.findById(i.getProductId()).get().getValue())
                .sum();
        Double semDesconto = items.stream().filter(i -> !productRepository.findById(i.getProductId()).get().getIsSale())
                .mapToDouble(i -> i.getQuantity() * productRepository.findById(i.getProductId()).get().getValue())
                .sum();
        return (comDesconto * 0.8) + semDesconto;
    }


    @Override
    public Set<Product> findProductsById(List<Long> ids) {
        return ids.stream()
                .map(id -> productRepository.findById(id))
                .filter(Optional -> Optional.isPresent())
                .map(Optional -> Optional.get())
                .collect(Collectors.toSet());

    }


    @Override
    public Double calculateMultipleOrders(List<List<OrderItem>> orders) {
        return orders.stream()
                .mapToDouble(o -> calculateOrderValue(o))
                .sum();

    }


    @Override
    public Map<Boolean, List<Product>> groupProductsBySale(List<Long> productIds) {
        return productIds.stream().filter(id -> id > 0 && id != null)
                .map(id -> productRepository.findById(id).get())
                .collect(Collectors.groupingBy(Product::getIsSale));


    }
}