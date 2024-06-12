package team07.airbnb.domain.booking.exception;

import team07.airbnb.common.exception.BadRequestException;

import java.time.LocalDate;

public class DateInversionException extends BadRequestException {

    public DateInversionException(LocalDate before, LocalDate after) {
        super("날짜가 잘못 지정되었습니다!", "Before Date : {%s}, After Date : {%s}".formatted(before.toString(), after.toString()));
    }
}
