package team07.airbnb.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team07.airbnb.domain.accommodation.dto.AccommodationListResponse;
import team07.airbnb.domain.accommodation.entity.AccommodationEntity;
import team07.airbnb.domain.product.dto.ProductListResponse;
import team07.airbnb.domain.product.entity.ProductEntity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static team07.airbnb.domain.product.entity.ProductStatus.OPEN;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductListResponse> findAvailableInDateRange(
            List<AccommodationEntity> accommodations, LocalDate checkIn, LocalDate checkOut) {

        List<Long> accomodationIds = accommodations.stream().map(AccommodationEntity::getId).toList();

        Map<Long, List<ProductEntity>> products = productRepository.findAllByAccommodationIdInAndStatus(accomodationIds, OPEN)
                .stream()
                .collect(Collectors.groupingBy(p -> p.getAccommodation().getId()));

        Map<Long, List<ProductEntity>> availableProducts = products.entrySet().stream()
                .filter(entry -> isAvailableInDateRange(entry.getValue(), checkIn, checkOut))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Calculate average price per day and Make Response List
        return availableProducts.keySet().stream().map(key -> {
                    List<ProductEntity> productEntities = availableProducts.get(key);
                    return new ProductListResponse(
                            AccommodationListResponse.of(productEntities.get(0).getAccommodation()),
                            (int) productEntities.stream().mapToInt(ProductEntity::getPrice).average().getAsDouble()
                    );
                }
        ).toList();
    }

    private boolean isAvailableInDateRange(List<ProductEntity> products, LocalDate checkIn, LocalDate checkOut) {
        Set<LocalDate> dateSet = new HashSet<>();
        for (LocalDate date = checkIn; !date.isAfter(checkOut); date = date.plusDays(1)) {
            dateSet.add(date);
        }

        for (ProductEntity product : products) {
            dateSet.remove(product.getDate());
        }

        return dateSet.isEmpty();
    }
}
