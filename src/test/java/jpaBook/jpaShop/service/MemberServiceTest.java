package jpaBook.jpaShop.service;


import jpaBook.jpaShop.Repository.MemberRepositoryOld;
import jpaBook.jpaShop.domain.Member;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepositoryOld memberRepository;

    @Test
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);


        //then
        assertEquals(member, memberService.findMember(savedId));
    }
    @Test(expected = IllegalStateException.class)
    public void 중복회원예외() throws Exception {
        //given

        Member member1 = new Member();
        member1.setName("member1");


        Member member2 = new Member();
        member2.setName("member1");


        //when
        memberService.join(member1);

        memberService.join(member2);



        Assert.fail("예외가 발생해야 한다");


        //then

    }

}