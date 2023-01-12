package jpaBook.jpaShop.Repository;

import jpaBook.jpaShop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring data JPA
 * <Type, Id값(pk type)>
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    //select m from member m where m.name = ?
    List<Member> findByName(String name);
}
