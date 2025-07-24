package aifu.project.libraryweb.service;

import aifu.project.common_domain.entity.User;
import aifu.project.common_domain.entity.enums.Role;
import aifu.project.common_domain.exceptions.UserNotFoundException;
import aifu.project.common_domain.payload.ResponseMessage;
import aifu.project.common_domain.payload.UserShortDTO;
import aifu.project.common_domain.payload.UserSummaryDTO;
import aifu.project.libraryweb.repository.UserRepository;
import aifu.project.libraryweb.utils.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${user.baseUri}")
    private String useBaseUri;

    @Value("${internal.token}")
    private String token;

    public long countUsers() {
        return userRepository.getUsersCount();
    }

    public ResponseEntity<ResponseMessage> getUserList(int pageNumber, int size) {

        Pageable pageable = PageRequest.of(--pageNumber, size, Sort.by(Sort.Direction.ASC, "id"));

        Page<User> userPage = userRepository.findByRoleAndIsDeletedFalse(Role.USER, pageable);

        Map<String, Object> map = Util.getPageInfo(userPage);
        map.put("data", getUserShortDTO(userPage.getContent()));

        return ResponseEntity.ok(new ResponseMessage(true, "User list", map));
    }

    public ResponseEntity<ResponseMessage> getSearchUserList(int pageNumber,
                                                             int size,
                                                             Long id,
                                                             String phone,
                                                             String sortBy,
                                                             String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(Sort.Direction.ASC, sortBy) : Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(--pageNumber, size, sort);

        Page<User> userPage;
        if (id != null) {
            userPage = userRepository.findByIdAndRoleAndIsDeletedFalse(id, Role.USER, pageable);
        }
//        else if (phone != null) {
//            userPage = userRepository.findByPhoneAndRoleAndIsDeletedFalse(phone, Role.USER, pageable);
//        }
        else {
            userPage = userRepository.findByRoleAndIsDeletedFalse(Role.USER, pageable);
        }

        Map<String, Object> map = Util.getPageInfo(userPage);
        map.put("data", getUserShortDTO(userPage.getContent()));

        return ResponseEntity.ok(new ResponseMessage(true, "User list", map));
    }

    public ResponseEntity<ResponseMessage> getUsersByStatus(int pageNumber, int size) {
        Pageable pageable = PageRequest.of(--pageNumber, size, Sort.by(Sort.Direction.ASC, "id"));

        Page<User> userPage = userRepository.findByRoleAndIsActiveAndIsDeletedFalse(Role.USER, false, pageable);

        Map<String, Object> map = Util.getPageInfo(userPage);
        map.put("data", getUserShortDTO(userPage.getContent()));

        return ResponseEntity.ok(new ResponseMessage(true, "User list", map));
    }

    public ResponseEntity<ResponseMessage> getUser(String id) {
        User user = userRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new UserNotFoundException("User not found by id:" + id));

        UserSummaryDTO dto = new UserSummaryDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getDegree(),
                user.getFaculty(),
                user.getChatId(),
                user.isActive()
        );

        return ResponseEntity.ok(new ResponseMessage(true, "Detailed user information", dto));
    }


    private List<UserShortDTO> getUserShortDTO(List<User> users) {
        return users.stream()
                .map(user -> new UserShortDTO(user.getId(), user.getName(),
                        user.getSurname(), user.getCardNumber(), user.isActive()))
                .toList();
    }

    public ResponseEntity<ResponseMessage> deleteUser(Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Internal-Token", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                useBaseUri + "/" + id,
                HttpMethod.DELETE,
                entity,
                ResponseMessage.class);
    }

    public User findByCardNumber(String cardNumber) {
        return userRepository.findUserByCardNumberAndIsActiveTrueAndIsDeletedFalse(cardNumber)
                .orElseThrow(() -> new UserNotFoundException("User not found by card number: " + cardNumber));
    }
}
