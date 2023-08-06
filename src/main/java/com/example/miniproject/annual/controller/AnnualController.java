package com.example.miniproject.annual.controller;

import com.example.miniproject.annual.dto.AnnualRequestDto;
import com.example.miniproject.annual.dto.AnnualResponseDto;
import com.example.miniproject.annual.service.AnnualService;
import com.example.miniproject.member.domain.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AnnualController {
    private final AnnualService annualService;

    @GetMapping("/main")
    public List<AnnualResponseDto.MainDto> main() {
        List<AnnualResponseDto.MainDto> mainDtos = annualService.findAll();

        return mainDtos;
    }

    @PostMapping("/annual")
    public ResponseEntity<?> save(@RequestBody @Valid AnnualRequestDto.SaveDto saveDto, @AuthenticationPrincipal
    PrincipalDetails principalDetails) {
        annualService.createAnnual(saveDto, principalDetails.getUsername());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/annual/cancel")
    public ResponseEntity<?> cancel(@RequestBody @Valid AnnualRequestDto.CancelDto cancelDto,
                                    @AuthenticationPrincipal PrincipalDetails principalDetails) {
        annualService.deleteAnnual(cancelDto, principalDetails.getUsername());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/annual/update")
    public ResponseEntity<?> update(@RequestBody @Valid AnnualRequestDto.UpdateDto updateDto,
                                    @AuthenticationPrincipal PrincipalDetails principalDetails) {

        annualService.updateAnnual(updateDto, principalDetails.getUsername());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public AnnualResponseDto.MyPageDto myPage(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        AnnualResponseDto.MyPageDto myPageDto = annualService.findAnnualsByMember(principalDetails.getUsername());

        return myPageDto;
    }

    @PostMapping("/user")
    public ResponseEntity<?> passwordUpdate(@RequestBody AnnualRequestDto.UpdatePasswordDto updatePasswordDto,
                               @AuthenticationPrincipal PrincipalDetails principalDetails) {

        annualService.updatePassword(updatePasswordDto, principalDetails.getUsername());

        return ResponseEntity.ok().build();
    }
}
