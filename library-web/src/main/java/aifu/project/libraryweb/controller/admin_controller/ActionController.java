package aifu.project.libraryweb.controller.admin_controller;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.action_dto.ExtendAcceptActionDTO;
import aifu.project.common_domain.dto.action_dto.ExtendRejectActionDTO;
import aifu.project.common_domain.dto.action_dto.WarningActionDTO;
import aifu.project.libraryweb.service.action_service.ActionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/actions")
@RequiredArgsConstructor
public class ActionController {
    private final ActionService actionService;

    @PostMapping("/extend/accept")
    @Operation(summary = "Booking vaqtini uzaytirishni tasdiqlash",
            description = """
                    notificationId -> notifationId yuboriladi,
                    extendDays -> booking ni uzaytirish uchun kunlar soni yuboriladi
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking vaqtini uzaytirish muvaffaqiyatli amalga oshirildi"),
            @ApiResponse(responseCode = "404", description = "Notification | Booking topilmadi"),
    })
    public ResponseEntity<ResponseMessage> extend(@RequestBody ExtendAcceptActionDTO extendAcceptActionDTO) {
        return actionService.extendAccept(extendAcceptActionDTO);
    }

    @PostMapping("/extend/reject")
    @Operation(summary = "Booking vaqtini uzaytirishni rad etish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking vaqtini uzaytirish rad etildi"),
            @ApiResponse(responseCode = "404", description = "Notification topilmadi"),
    })
    public ResponseEntity<ResponseMessage> extendReject(@RequestBody ExtendRejectActionDTO extendRejectDTO) {
        return actionService.extendReject(extendRejectDTO);
    }

    @PostMapping("/warning")
    @Operation(summary = "Warning notification ni ochirish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Warning notification muvaffaqiyatli ochirildi"),
            @ApiResponse(responseCode = "404", description = "Notification topilmadi"),
    })
    public ResponseEntity<ResponseMessage> warning(@RequestBody WarningActionDTO warningActionDTO) {
        return actionService.warning(warningActionDTO);
    }
}
