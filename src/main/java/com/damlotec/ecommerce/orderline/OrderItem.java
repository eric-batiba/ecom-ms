package com.damlotec.ecommerce.orderline;

import com.damlotec.ecommerce.order.Order;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
@Builder
@Entity
@Table(name = "order_item")
public class OrderItem {
    @Id
    @SequenceGenerator(name = "order_item_seq", sequenceName = "order_item_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_seq")
    private Integer id;
    private int productId;
    private double quantity;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
