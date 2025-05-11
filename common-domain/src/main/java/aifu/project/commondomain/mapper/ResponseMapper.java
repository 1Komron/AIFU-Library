package aifu.project.commondomain.mapper;

import aifu.project.commondomain.payload.*;

public class ResponseMapper {

    public static BorrowBookResponse borrowBookResponseToWeb(BorrowBookResponseWeb response) {
        return new BorrowBookResponse(response.chatId(), response.bookId(), response.accept());
    }

    public static ReturnBookResponse returnBookResponseToWeb(ReturnBookResponse response) {
        return new ReturnBookResponse(response.chatId(), response.bookId(), response.accept());
    }

    public static ExtendBookResponse extendBookResponseToWeb(ExtendBookResponseWeb response) {
        return new ExtendBookResponse(response.chatId(), response.bookId(), response.accept());
    }

    public static RegistrationResponse registrationResponseToWeb(RegistrationResponseWeb response) {
        return new RegistrationResponse(response.chatId(), response.accept());
    }

    private ResponseMapper() {
    }
}
