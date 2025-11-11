package ru.practicum.category.mapper;

import org.mapstruct.Mapper;
import interaction.model.category.in.NewCategoryDto;
import interaction.model.category.output.CategoryDto;
import ru.practicum.category.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);

    Category toCategory(NewCategoryDto category);
}