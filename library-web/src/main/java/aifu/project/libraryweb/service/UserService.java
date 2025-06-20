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
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${user.BaseUri}")
    private String useBaseUri;

    @Value("${internal.token}")
    private String token;

    public long countUsers() {
        return userRepository.getUsersCount();
    }

    public ResponseEntity<ResponseMessage> getUserList(int pageNumber, int size) {
        Pageable pageable = PageRequest.of(--pageNumber, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<User> userPage = userRepository.findByRoleAndIsDeletedFalse(Role.USER, pageable);

        Map<String, Object> map = Map.of(
                "data", getUserShortDTO(userPage.getContent()),
                "pageInfo", Util.getPageInfo(userPage)
        );

        return ResponseEntity.ok(new ResponseMessage(true, "User list", map));
    }

    public ResponseEntity<ResponseMessage> getInactiveUsers(int pageNumber, int size) {
        Pageable pageable = PageRequest.of(--pageNumber, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<User> userPage = userRepository.findByRoleAndIsActiveAndIsDeletedFalse(Role.USER, false, pageable);

        Map<String, Object> map = Map.of(
                "data", getUserShortDTO(userPage.getContent()),
                "pageInfo", Util.getPageInfo(userPage)
        );

        return ResponseEntity.ok(new ResponseMessage(true, "User list", map));
    }

    public ResponseEntity<ResponseMessage> getUser(String id) {
        User user = userRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new UserNotFoundException("User not found by id:" + id));

        UserSummaryDTO dto = new UserSummaryDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getPhone(),
                user.getFaculty(),
                user.getCourse(),
                user.getGroup(),
                user.getChatId(),
                user.isActive()
        );

        return ResponseEntity.ok(new ResponseMessage(true, "Detailed user information", dto));
    }


    private List<UserShortDTO> getUserShortDTO(List<User> users) {
        return users.stream()
                .map(user -> new UserShortDTO(user.getId(), user.getName(), user.getSurname(), user.getPhone()))
                .toList();
    }

    public ResponseEntity<ResponseMessage> deleteUser(Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Internal-Token", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<ResponseMessage> exchange = restTemplate.exchange(
                useBaseUri + "/" + id,
                HttpMethod.DELETE,
                entity,
                ResponseMessage.class);

        return exchange;
    }
}
