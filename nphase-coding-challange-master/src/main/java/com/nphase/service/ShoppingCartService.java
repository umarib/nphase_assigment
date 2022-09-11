package com.nphase.service;

import com.nphase.entity.Product;
import com.nphase.entity.ShoppingCart;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShoppingCartService {

    public BigDecimal calculateTotalPrice(ShoppingCart shoppingCart) {
        return shoppingCart.getProducts()
                .stream()
                .map(product -> product.getPricePerUnit().multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    /* ***************** Task 2 ****************************
        We would like to reward the clients that are buying products in bulk.
        If the client buys more than 3 items of the same product, we are giving him 10% discount for this product.
      */
    public BigDecimal discountOnProduct(ShoppingCart shoppingCart,final double discountPercentage,final int discountIfItemMoreThan){
        BigDecimal discount = new BigDecimal(0.0);
        BigDecimal totalPrice = new BigDecimal(0.0);
        totalPrice = shoppingCart.getProducts()
                .stream().map(product->{
                    if(product.getQuantity()>discountIfItemMoreThan){
                        BigDecimal itemPrice = product.getPricePerUnit().multiply(BigDecimal.valueOf(product.getQuantity()));
                        return itemPrice.subtract(itemPrice.multiply(new BigDecimal(discountPercentage/100))).setScale(2, RoundingMode.HALF_EVEN);
                    }else{
                        return product.getPricePerUnit().multiply(BigDecimal.valueOf(product.getQuantity())).setScale(2, RoundingMode.HALF_EVEN);
                    }
                }).reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
        return totalPrice;
    }

    /* ***************** Task 3 ****************************
       We would like to introduce the concept of item category and expand our discount policies to the entire category.
        If the client buys more than 3 items of the product within the same category, we are giving him 10% discount for all product in this category.
     */
    public BigDecimal discountOnSameItemCategory(ShoppingCart shoppingCart,final double discountPercentage,final int discountIfItemMoreThan) {
        Map<String, List<Product>> categoryGroup =  shoppingCart.getProducts().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getCategory(), e))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
        BigDecimal totalPrice = new BigDecimal(0.0);

        for (Map.Entry<String, List<Product>> entry : categoryGroup.entrySet()) {
            Integer totalCount = entry.getValue().stream().map(prodItem->{
                return prodItem.getQuantity();
            }).reduce(Integer::sum).orElse(0);
            BigDecimal totalCatPrice = new BigDecimal(0.0);
            if(totalCount>discountIfItemMoreThan){
                totalCatPrice = entry.getValue().stream().map(prodItem->{
                            BigDecimal itemPrice = prodItem.getPricePerUnit().multiply(BigDecimal.valueOf(prodItem.getQuantity()));
                            return itemPrice.subtract(itemPrice.multiply(new BigDecimal(discountPercentage/100))).setScale(2, RoundingMode.HALF_EVEN);
                        }).reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO);
            }else{
                totalCatPrice = entry.getValue().stream().map(prodItem->{
                            return prodItem.getPricePerUnit().multiply(BigDecimal.valueOf(prodItem.getQuantity())).setScale(2, RoundingMode.HALF_EVEN);
                        }).reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO);
            }
//            System.out.println("***************** "+entry.getKey() +" ***************** Count = "+totalCount +" totalCatPrice = "+totalCatPrice);
            totalPrice = totalPrice.add(totalCatPrice);
        }
//        System.out.println(totalPrice);
        return totalPrice;
    }


    public static void task4(){
        System.out.println("********** Task 4 **********");
        System.out.println("Discount percentage and itemCount already passed as parameters in Task2 and Task3");
    }
}
