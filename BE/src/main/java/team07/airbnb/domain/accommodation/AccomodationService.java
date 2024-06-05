package team07.airbnb.domain.accommodation;

import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import team07.airbnb.domain.accommodation.dto.AccomodationListResponse;
import team07.airbnb.domain.accommodation.entity.AccomodationEntity;
import team07.airbnb.util.GeometryHelper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccomodationService {

    private final GeometryHelper geometryHelper;
    private final AccomodationRepository accomodationRepository;

    public List<AccomodationEntity> findNearbyAccommodations(double longitude, double latitude, double distance) {
        Point center = geometryHelper.getPoint(longitude, latitude);

        return accomodationRepository.findByLocationWithinDistance(center, distance);
    }

    public List<AccomodationEntity> findAllAccommodations() {
        return accomodationRepository.findAll();
    }
}
