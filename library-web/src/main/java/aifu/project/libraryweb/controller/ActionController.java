package aifu.project.libraryweb.controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.action_dto.ExtendAcceptActionDTO;
import aifu.project.common_domain.dto.action_dto.ExtendRejectActionDTO;
import aifu.project.common_domain.dto.action_dto.WarningActionDTO;
import aifu.project.libraryweb.service.ActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/actions")
@RequiredArgsConstructor
public class ActionController {
    private final ActionService actionService;

    @PostMapping("/extend/accept")
    public ResponseEntity<ResponseMessage> extend(ExtendAcceptActionDTO extendAcceptActionDTO) {
        return actionService.extendAccept(extendAcceptActionDTO);
    }

    @PostMapping("/extend/reject")
    public ResponseEntity<ResponseMessage> extendReject(ExtendRejectActionDTO extendRejectDTO) {
        return actionService.extendReject(extendRejectDTO);
    }

    @PostMapping("/warning")
    public ResponseEntity<ResponseMessage> warning(WarningActionDTO warningActionDTO) {
        return actionService.warning(warningActionDTO);
    }
}
