package com.example.ecommerce.service;

import com.example.ecommerce.domain.Member;
import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.MemberResponse;
import com.example.ecommerce.dto.SignupRequest;
import com.example.ecommerce.dto.TokenResponse;
import com.example.ecommerce.global.exception.EmailAlreadyExistsException;
import com.example.ecommerce.global.exception.InvalidCredentialsException;
import com.example.ecommerce.global.exception.MemberNotFoundException;
import com.example.ecommerce.global.jwt.JwtTokenProvider;
import com.example.ecommerce.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; // EncoderConfig에서 등록한 BCrypt 빈이 주입됨
    private final JwtTokenProvider jwtTokenProvider;

    public MemberService(MemberRepository memberRepository,
                         PasswordEncoder passwordEncoder,
                         JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 회원가입. 쓰기 작업이므로 클래스의 readOnly를 덮어써 @Transactional(쓰기)로.
    @Transactional
    public MemberResponse signup(SignupRequest request) {
        // 1) 이메일 중복 검사
        if (memberRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        // 2) 비밀번호를 BCrypt로 암호화 (평문을 절대 저장하지 않는다)
        String encodedPassword = passwordEncoder.encode(request.password());

        // 3) 저장 (Member.create는 role을 USER로 고정)
        Member member = memberRepository.save(
                Member.create(request.email(), encodedPassword, request.name()));

        return MemberResponse.from(member);
    }

    // 로그인. 성공하면 JWT를 발급해 반환한다. (읽기 전용 트랜잭션으로 충분)
    public TokenResponse login(LoginRequest request) {
        // 1) 이메일로 회원 조회. 없으면 → InvalidCredentials (존재 여부를 숨김)
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        // 2) 입력 비밀번호(평문)와 저장된 해시를 비교.
        //    matches()가 저장된 해시 안의 salt를 꺼내 같은 방식으로 해싱한 뒤 비교해준다.
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new InvalidCredentialsException();
        }

        // 3) 회원 id + role을 담은 JWT 발급
        String token = jwtTokenProvider.createToken(member.getId(), member.getRole());
        return TokenResponse.bearer(token);
    }

    // 내 정보 조회. 필터가 SecurityContext에 심어둔 회원 id로 조회한다.
    public MemberResponse getMyInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        return MemberResponse.from(member);
    }
}
