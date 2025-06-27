package jpabook.jpashop.api;

import jakarta.validation.Valid;
import java.util.List;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

  private final MemberService memberService;

  /*
  * 등록 V1: 엔티티를 RequestBody에 직접 매핑
  * 문제점
  *   - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
  *   - 엔티티에 API 검증을 위한 로직이 들어간다. (@NotEmpty 등등)
  *   - 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데
  *   - 한 엔티티에 각각의 API를 위한 모든 요청, 요구사항을 담기는 어렵다.
  *   - 엔티티가 변경되면 API 스펙이 변한다.
  * 결론
  *   - API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받는다.
  * */
  @PostMapping("/api/v1/members")
  public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
    Long id = memberService.join(member);
    return new CreateMemberResponse(id);
  }

  // 등록 V2: 엔티티 대신 DTO를 RequestBody에 매핑
  @PostMapping("/api/v2/members")
  public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
    Member member = new Member();
    member.setName(request.name);
    member.setAddress(request.address);
    Long id = memberService.join(member);
    return new CreateMemberResponse(id);
  }

  @PutMapping("/api/v2/members/{id}")
  public UpdateMemberResponse updateMemberV2(
      @PathVariable Long id,
      @RequestBody @Valid UpdateMemberRequest request
  ) {
    memberService.update(id, request.name);
    return new UpdateMemberResponse(id, request.name);
  }

  /**
   * 조회 V1: 응답 값으로 엔티티를 직접 외부에 노출한다.
   * 문제점
   * - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
   * - 기본적으로 엔티티의 모든 값이 노출된다.
   * - 응답 스펙을 맞추기 위해 로직이 추가된다. (@JsonIgnore, 별도의 뷰 로직 등등)
   * - 실무에서는 같은 엔티티에 대해 API가 용도에 따라 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위한 프레젠테이션 응답 로직을 담기는 어렵다.
   * - 엔티티가 변경되면 API 스펙이 변한다.
   * - 추가로 컬렉션을 직접 반환하면 항후 API 스펙을 변경하기 어렵다.(별도의 Result 클래스 생성으로 해결)
   * 결론
   * - API 응답 스펙에 맞추어 별도의 DTO를 반환한다.
   */
  //조회 V1: 안 좋은 버전, 모든 엔티티가 노출, @JsonIgnore -> 이건 정말 최악, api가 이거 하나인가! 화면에 종속적이지 마라!
  @GetMapping("/api/v1/members")
  public List<Member> membersV1() {
    return memberService.findMembers();
  }

  // 조회 V2: 응답 값으로 엔티티가 아닌 별도의 DTO를 반환한다.
  @GetMapping("/api/v2/members")
  public Result<List<MemberDto>> membersV2() {
    return new Result<>(memberService
        .findMembers()
        .stream()
        .map(m -> new MemberDto(m.getName()))
        .toList());
  }

  public record CreateMemberRequest(String name, Address address) {

  }

  public record CreateMemberResponse(Long id) {

  }

  public record UpdateMemberRequest(String name) {

  }

  public record UpdateMemberResponse(Long id, String name) {

  }

  public record Result<T>(T data) {

  }

  public record MemberDto(String name) {


  }

}
