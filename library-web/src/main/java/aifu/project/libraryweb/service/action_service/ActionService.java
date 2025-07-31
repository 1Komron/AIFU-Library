package aifu.project.libraryweb.service.action_service;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.action_dto.ExtendAcceptActionDTO;
import aifu.project.common_domain.dto.action_dto.ExtendRejectActionDTO;
import aifu.project.common_domain.dto.action_dto.WarningActionDTO;
import org.springframework.http.ResponseEntity;

public interface ActionService {
    ResponseEntity<ResponseMessage> extendReject(ExtendRejectActionDTO extendRejectActionDTO);

    ResponseEntity<ResponseMessage> extendAccept(ExtendAcceptActionDTO extendAcceptActionDTO);

    ResponseEntity<ResponseMessage> warning(WarningActionDTO warningActionDTO);
}
