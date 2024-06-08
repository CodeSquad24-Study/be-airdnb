package team07.airbnb.domain.booking.price_policy.discount;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import team07.airbnb.domain.BaseEntity;

@Entity
@Table(name = "DISCOUNT_POLICY")
public class DiscountPolicyEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String description;

    @NotNull
    private String policyBeanName;

}
