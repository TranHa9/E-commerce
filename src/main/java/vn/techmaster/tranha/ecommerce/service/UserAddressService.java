package vn.techmaster.tranha.ecommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import vn.techmaster.tranha.ecommerce.entity.User;
import vn.techmaster.tranha.ecommerce.entity.UserAddress;
import vn.techmaster.tranha.ecommerce.exception.ObjectNotFoundException;
import vn.techmaster.tranha.ecommerce.model.request.CreateUserAddressRequest;
import vn.techmaster.tranha.ecommerce.model.request.UpdateUserAddressRequest;
import vn.techmaster.tranha.ecommerce.model.response.UserAddressResponse;
import vn.techmaster.tranha.ecommerce.repository.UserAddressRepository;
import vn.techmaster.tranha.ecommerce.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAddressService {
    ObjectMapper objectMapper;
    UserAddressRepository userAddressRepository;
    UserRepository userRepository;

    public List<UserAddressResponse> getAddressAll() {
        return userAddressRepository.findAll().stream()
                .map(userAddress -> objectMapper.convertValue(userAddress, UserAddressResponse.class))
                .toList();
    }

    public UserAddressResponse getUserAddressById(Long id) throws ObjectNotFoundException {
        return userAddressRepository.findById(id)
                .map(userAddress -> objectMapper.convertValue(userAddress, UserAddressResponse.class))
                .orElseThrow(() -> new ObjectNotFoundException("UserAddress not found"));
    }

    public UserAddressResponse create(CreateUserAddressRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserAddress userAddress = UserAddress.builder()
                .address(request.getAddress())
                .phone(request.getPhone())
                .defaultAddress(false)
                .user(user)
                .build();
        userAddress = userAddressRepository.save(userAddress);
        return objectMapper.convertValue(userAddress, UserAddressResponse.class);
    }

    public UserAddressResponse update(Long id, UpdateUserAddressRequest request) {
        UserAddress userAddress = userAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserAddress not found"));
        userAddress.setPhone(request.getPhone());
        userAddress.setAddress(request.getAddress());
        userAddress = userAddressRepository.save(userAddress);
        return objectMapper.convertValue(userAddress, UserAddressResponse.class);
    }

    public void delete(Long id) {
        UserAddress userAddress = userAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserAddress not found"));
        userAddressRepository.delete(userAddress);
    }

    public UserAddressResponse updateDefaultAddress(Long id) throws ObjectNotFoundException {
        
        UserAddress userAddress = userAddressRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("UserAddress not found"));

        User user = userAddress.getUser();

        // Đặt tất cả địa chỉ của user này về false
        List<UserAddress> allAddresses = userAddressRepository.findByUserId(user.getId());
        allAddresses.forEach(address -> address.setDefaultAddress(false));
        userAddressRepository.saveAll(allAddresses);

        // Cập nhật bản ghi được chọn thành true
        userAddress.setDefaultAddress(true);
        userAddress = userAddressRepository.save(userAddress);

        return objectMapper.convertValue(userAddress, UserAddressResponse.class);
    }
}
