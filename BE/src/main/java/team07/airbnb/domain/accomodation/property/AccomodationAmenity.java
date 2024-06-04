package team07.airbnb.domain.accomodation.property;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccomodationAmenity {
    private int bed = 0;
    private int bedroom = 0;
    private int bathroom = 0;
    private boolean kitchen = false;
    private boolean internet = false;
    private boolean airConditioner = false;
}