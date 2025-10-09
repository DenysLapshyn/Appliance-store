package Onlinestore.mapper;

import Onlinestore.dto.GetItemDTO;
import Onlinestore.dto.NewItemDTO;
import Onlinestore.dto.UpdateItemDTO;
import Onlinestore.entity.Item;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-09T12:35:34+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class ItemMapperImpl implements ItemMapper {

    @Override
    public Item newItemDTOToItem(NewItemDTO newItemDTO) {
        if ( newItemDTO == null ) {
            return null;
        }

        Item item = new Item();

        item.setName( newItemDTO.getName() );
        item.setPrice( newItemDTO.getPrice() );
        item.setAmount( newItemDTO.getAmount() );
        item.setDescription( newItemDTO.getDescription() );
        item.setLogoName( newItemDTO.getLogoName() );
        Set<String> set = newItemDTO.getImageNames();
        if ( set != null ) {
            item.setImageNames( new LinkedHashSet<String>( set ) );
        }
        Map<String, String> map = newItemDTO.getSpecs();
        if ( map != null ) {
            item.setSpecs( new LinkedHashMap<String, String>( map ) );
        }

        return item;
    }

    @Override
    public UpdateItemDTO itemToUpdateItemDTO(Item item) {
        if ( item == null ) {
            return null;
        }

        UpdateItemDTO updateItemDTO = new UpdateItemDTO();

        updateItemDTO.setId( item.getId() );
        updateItemDTO.setName( item.getName() );
        updateItemDTO.setPrice( item.getPrice() );
        updateItemDTO.setAmount( item.getAmount() );
        updateItemDTO.setDescription( item.getDescription() );
        updateItemDTO.setLogoName( item.getLogoName() );
        Set<String> set = item.getImageNames();
        if ( set != null ) {
            updateItemDTO.setImageNames( new LinkedHashSet<String>( set ) );
        }
        Map<String, String> map = item.getSpecs();
        if ( map != null ) {
            updateItemDTO.setSpecs( new LinkedHashMap<String, String>( map ) );
        }

        return updateItemDTO;
    }

    @Override
    public Item updateItemDTOToItem(UpdateItemDTO updateItemDTO) {
        if ( updateItemDTO == null ) {
            return null;
        }

        Item item = new Item();

        item.setId( updateItemDTO.getId() );
        item.setName( updateItemDTO.getName() );
        item.setPrice( updateItemDTO.getPrice() );
        item.setAmount( updateItemDTO.getAmount() );
        item.setDescription( updateItemDTO.getDescription() );
        item.setLogoName( updateItemDTO.getLogoName() );
        Set<String> set = updateItemDTO.getImageNames();
        if ( set != null ) {
            item.setImageNames( new LinkedHashSet<String>( set ) );
        }
        Map<String, String> map = updateItemDTO.getSpecs();
        if ( map != null ) {
            item.setSpecs( new LinkedHashMap<String, String>( map ) );
        }

        return item;
    }

    @Override
    public GetItemDTO itemToGetItemDTO(Item item) {
        if ( item == null ) {
            return null;
        }

        GetItemDTO getItemDTO = new GetItemDTO();

        getItemDTO.setId( item.getId() );
        getItemDTO.setName( item.getName() );
        getItemDTO.setPrice( item.getPrice() );
        getItemDTO.setAmount( item.getAmount() );
        getItemDTO.setDescription( item.getDescription() );
        getItemDTO.setLogoName( item.getLogoName() );
        Set<String> set = item.getImageNames();
        if ( set != null ) {
            getItemDTO.setImageNames( new LinkedHashSet<String>( set ) );
        }
        Map<String, String> map = item.getSpecs();
        if ( map != null ) {
            getItemDTO.setSpecs( new LinkedHashMap<String, String>( map ) );
        }

        return getItemDTO;
    }

    @Override
    public List<GetItemDTO> itemListToGetItemDTOList(List<Item> items) {
        if ( items == null ) {
            return null;
        }

        List<GetItemDTO> list = new ArrayList<GetItemDTO>( items.size() );
        for ( Item item : items ) {
            list.add( itemToGetItemDTO( item ) );
        }

        return list;
    }
}
