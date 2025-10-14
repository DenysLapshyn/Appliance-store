package Onlinestore.mapper;

import Onlinestore.dto.GetItemDTO;
import Onlinestore.dto.NewItemDTO;
import Onlinestore.dto.UpdateItemDTO;
import Onlinestore.entity.Item;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item newItemDTOToItem(NewItemDTO newItemDTO);

    UpdateItemDTO itemToUpdateItemDTO(Item item);

    Item updateItemDTOToItem(UpdateItemDTO updateItemDTO);

    GetItemDTO itemToGetItemDTO(Item item);

    List<GetItemDTO> itemListToGetItemDTOList(List<Item> items);

}
