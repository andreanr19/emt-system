package co.edu.icesi.emt.auth.application.controller.authentication;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.icesi.emt.auth.application.dto.login.LoginRequestDTO;
import co.edu.icesi.emt.auth.application.dto.login.LoginResponseDTO;
import co.edu.icesi.emt.auth.application.dto.resetpassword.ResetPasswordRequestDTO;
import co.edu.icesi.emt.auth.application.dto.signup.SignupRequestDTO;
import co.edu.icesi.emt.auth.application.service.user.UserService;
import co.edu.icesi.emt.auth.security.jwt.JWTProvider;
import co.edu.icesi.emt.auth.util.validators.UserAdminValidator;
import co.edu.icesi.emt.auth.util.validators.exceptions.UserIsNotAdminException;

@RestController
@RequestMapping("/public/auth")
public class AuthenticationController {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JWTProvider jwtProvider;

    private final UserAdminValidator userAdminValidator;

    @Autowired
    public AuthenticationController(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
            UserService userService, JWTProvider jwtProvider, UserAdminValidator userAdminValidator) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.userAdminValidator = userAdminValidator;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody final SignupRequestDTO signUpRequestDTO,
            final HttpServletRequest httpRequest) throws UserIsNotAdminException {

        userAdminValidator.validate(httpRequest);

        userService.save(signUpRequestDTO.getUsername(), passwordEncoder.encode(signUpRequestDTO.getPassword()));

        return new ResponseEntity<String>(
                "User created: " + userService.findByUsername(signUpRequestDTO.getUsername()).toString(),
                HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody final LoginRequestDTO loginRequestDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJWT(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return new ResponseEntity<LoginResponseDTO>(
                new LoginResponseDTO(loginRequestDTO.getUsername(), jwt, userDetails.getAuthorities()), HttpStatus.OK);
    }

    @PutMapping("/password")
    public ResponseEntity<String> changePassword(@RequestBody final ResetPasswordRequestDTO resetPasswordRequestDTO,
            final HttpServletRequest httpRequest) throws UserIsNotAdminException {

        userAdminValidator.validate(httpRequest);

        userService.changePassword(resetPasswordRequestDTO.getUsername(),
                passwordEncoder.encode(resetPasswordRequestDTO.getPassword()));

        return new ResponseEntity<String>(
                "User password changed: " + userService.findByUsername(resetPasswordRequestDTO.getUsername()).toString(),
                HttpStatus.OK);
    }
}