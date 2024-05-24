package world.inetumrealdolmen.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import world.inetumrealdolmen.domain.Tag;
import world.inetumrealdolmen.dto.TagDto;

/**
 * Provides static methods to map between {@link Tag} objects and {@link TagDto} objects.
 */
public class TagMapper {

  /**
   * Maps a Tag object to a TagDto.Index object.
   *
   * @param tag The Tag object to be mapped.
   * @return A TagDto.Index object mapped from the given Tag.
   */
  public static TagDto.Index toDto(Tag tag) {
    var dto = new TagDto.Index();
    dto.name = tag.name;
    return dto;
  }

  /**
   * Maps a set of Tag objects to a set of TagDto.Index objects.
   *
   * @param tagList The set of Tag objects to be mapped.
   * @return A set of TagDto.Index objects mapped from the given set of Tag objects.
   */
  public static Set<TagDto.Index> toDto(Set<Tag> tagList) {
    return tagList.stream().map(TagMapper::toDto).collect(Collectors.toSet());
  }
}
