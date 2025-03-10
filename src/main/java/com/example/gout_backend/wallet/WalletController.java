package com.example.gout_backend.wallet;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gout_backend.wallet.dto.TourCompanyWalletInfoDto;
import com.example.gout_backend.wallet.dto.UserTopupDto;
import com.example.gout_backend.wallet.dto.UserWalletInfoDto;


@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final Logger logger = LoggerFactory.getLogger(WalletController.class);

    private final WalletService walletService;
    // User -> see own wallet

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    
    @GetMapping("/me")
    public ResponseEntity<UserWalletInfoDto> userGetOwnWallet (Authentication authentication) {
        var jwt = (Jwt) authentication.getPrincipal();
        var userId = jwt.getClaimAsString("sub"); 
        var wallet = walletService.getOwnWallet(Integer.valueOf(userId));
        return ResponseEntity.ok(wallet);


    }
    // User -> topup (Assume do via application, bank transfer on the background)
    @PostMapping("/topup")
    public ResponseEntity<UserWalletInfoDto>  userTopup(@RequestHeader("idempotent-key") String idempotentKey, 
                                                            @RequestBody @Validated UserTopupDto body, 
                                                            Authentication authentication
                                                        ) {
        var jwt = (Jwt) authentication.getPrincipal();
        var userId = jwt.getClaimAsString("sub");
        var recreatedBody = new UserTopupDto(body.amount(), Integer.valueOf(userId), idempotentKey);                                                   
        var result = walletService.topup(recreatedBody);
        return ResponseEntity.ok(result);
    }

    // Company -> see own waller
    public ResponseEntity<TourCompanyWalletInfoDto> tourCompanyGetOwnWallet () {

        return ResponseEntity.ok().build();
    }


    // Company -> withdraw
    public ResponseEntity<TourCompanyWalletInfoDto> withdrawMoneytoBankAccount () {

        return ResponseEntity.ok().build();
    }
}
