package jpaBook.jpaShop.service;


import jpaBook.jpaShop.Repository.MemberRepository;
import jpaBook.jpaShop.Repository.MemberRepositoryOld;
import jpaBook.jpaShop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;


    //회원 가입
//    @Transactional(readOnly = false)//기본값이 false라 인자 안넣어도 됨
    @Transactional
    public Long join(Member member){

        validateDupicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDupicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        //Exception occurs
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }

    }

    //회원 전체 조회

    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Member findMember(Long memberId){
        return memberRepository.findById(memberId).get();
    }


    @Transactional
    public void update(Long id, String name) {
        Member one = memberRepository.findById(id).get();
        one.setName(name);
    }
}
