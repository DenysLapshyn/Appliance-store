package Onlinestore.mapper;

import Onlinestore.dto.GetItemDTO;
import Onlinestore.dto.NewItemDTO;
import Onlinestore.dto.UpdateItemDTO;
import Onlinestore.entity.Item;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    public abstract Item newItemDTOToItem(NewItemDTO newItemDTO);

    public abstract UpdateItemDTO itemToUpdateItemDTO(Item item);

    public abstract Item updateItemDTOToItem(UpdateItemDTO updateItemDTO);

    public abstract GetItemDTO itemToGetItemDTO(Item item);

    public abstract List<GetItemDTO> itemListToGetItemDTOList(List<Item> items);

}
