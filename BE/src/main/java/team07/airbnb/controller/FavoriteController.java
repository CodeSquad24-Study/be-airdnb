package team07.airbnb.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team07.airbnb.common.auth.aop.Authenticated;
import team07.airbnb.domain.product.ProductService;
import team07.airbnb.domain.product.dto.ProductListResponse;
import team07.airbnb.entity.ProductEntity;
import team07.airbnb.domain.product.ProductStatus;
import team07.airbnb.domain.user.dto.FavoritesResponse;
import team07.airbnb.domain.user.dto.TokenUserInfo;
import team07.airbnb.entity.LikeEntity;
import team07.airbnb.domain.user.enums.Role;
import team07.airbnb.domain.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "위시리스트")
@RestController
@RequiredArgsConstructor
@RequestMapping("/favorite")
public class FavoriteController {

    private final UserService userService;
    private final ProductService productService;

    @Authenticated(Role.USER)
    @PostMapping("/{id}")
    public void addFavorite(@PathVariable long id, TokenUserInfo user){
        userService.addFavorite(user.id(), productService.findById(id));
    }

    @Authenticated(Role.USER)
    @DeleteMapping("/{id}")
    public void removeFavorite(@PathVariable long id, TokenUserInfo user){
        userService.removeFavorite(user.id(), productService.findById(id));

    }

    @Authenticated(Role.USER)
    @GetMapping
    public FavoritesResponse getMyWishList(TokenUserInfo user){
        List<ProductEntity> available = new ArrayList<>();
        List<ProductEntity> nonAvailable = new ArrayList<>();

        for(LikeEntity like : userService.getCompleteUser(user).getFavorites()){
            ProductEntity product = like.getProduct();
            if(product.getStatus() == ProductStatus.OPEN) available.add(product);
            else nonAvailable.add(product);
        }

        return new FavoritesResponse(
                available.stream().map(ProductListResponse::of).toList(),
                nonAvailable.stream().map(ProductListResponse::of).toList()
        );
    }
}
