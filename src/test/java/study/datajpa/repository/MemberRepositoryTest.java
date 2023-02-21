package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(true)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA", 10, null);
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);
    }


    public void basicCRUD() {
        Member member1 = new Member("member1", 10, null);
        Member member2 = new Member("member2", 20, null);

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member1.getId()).get();
        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member1);

        long deletedCount = memberRepository.count();
        Assertions.assertThat(deletedCount).isEqualTo(0);

    }


    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("member1", 10);
        Member m2 = new Member("member1", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("member1", 15);
        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("member1");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("member1", 10);
        Member m2 = new Member("member2", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("member1");
        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("member1");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(10);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }
    @Test
    public void testQuery() {
        Member m1 = new Member("member1", 10);
        Member m2 = new Member("member2", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("member1", 10);
        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("member1");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(10);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("member1", 10);
        Member m2 = new Member("member2", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findUsernameList();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Member m1 = new Member("member1", 10);
        Member m2 = new Member("member2", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames((Arrays.asList(m1.getUsername(), m2.getUsername())));
        for (Member member : result) {
            System.out.println(member.getUsername());
        }

    }

    @Test
    public void findByNames() {
        Team t1 = new Team("team1");
        teamRepository.save(t1);

        Member m1 = new Member("member1", 10);
        m1.setTeam(t1);
        memberRepository.save(m1);

        List<MemberDto> result = memberRepository.findMemberDto();
        for (MemberDto dto : result) {
            System.out.println("dto = " + dto);
        }
    }
    @Test
    public void returnType() {

        Member m1 = new Member("member1", 10);
        Member m2 = new Member("member2", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        Member findMembers = memberRepository.findMemberByUsername(m1.getUsername());
    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> members = memberRepository.findByAge(age, pageRequest);

        // then
        Assertions.assertThat(members.getContent().size()).isEqualTo(3);
        Assertions.assertThat(members.getTotalElements()).isEqualTo(5);
        Assertions.assertThat(members.getNumber()).isEqualTo(0);
        Assertions.assertThat(members.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(members.isFirst()).isTrue();
        Assertions.assertThat(members.hasNext()).isTrue();
    }
    @Test
    public void bulkUpdate() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        int resultCount = memberRepository.bulkAgePlus(20);

        // 벌크연산했을 때 영속성 컨텍스트를 초기화한다.
        em.flush();
        em.clear();

        Assertions.assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findEntityGraphByUsername(member1.getUsername());

        for (Member member : members) {
            System.out.println("member = "+member.getUsername());
        }
    }

    @Test
    public void queryHint() {
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        em.flush();
        em.clear();

        Member findMember = memberRepository.findReadonlyByUsername(member1.getUsername());
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    public void callCustom() {
        memberRepository.findMemberCustom();
    }
}