package ru.practicum.dto.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.practicum.model.Comment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentDtoMapper {

    Comment newCommentDtoToComment(NewCommentDto createDto);

    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "createdOn", target = "createdOn", qualifiedByName = "localDateTimeToString")
    @Mapping(source = "updated", target = "updated")
    CommentDto commentToCommentDto(Comment comment);

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
