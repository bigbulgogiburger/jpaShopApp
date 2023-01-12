package jpaBook.jpaShop.Repository;

import jpaBook.jpaShop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        if(item.getId() == null){
//          처음에 아이템 아이디 없음.(새로 생긴 객체) ==>추가
            em.persist(item);
        }else{
//          아이템 값이 있으면 디비에 등록된 것임.. merge 는 update와 비슷
            em.merge(item);
        }

    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i",Item.class).getResultList();
    }
}
