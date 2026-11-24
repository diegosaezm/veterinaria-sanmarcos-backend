
package Backend.Veterinaria.San.Marcos.controller;

import Backend.Veterinaria.San.Marcos.model.Usuario;
import Backend.Veterinaria.San.Marcos.model.Rol;

import Backend.Veterinaria.San.Marcos.repository.UsuarioRepository;

import Backend.Veterinaria.San.Marcos.service.UsuarioService;
import Backend.Veterinaria.San.Marcos.service.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import java.util.Locale;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            UsuarioService usuarioService,
            UsuarioRepository usuarioRepository,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public record RegistroRequest(
        String correo,
        String contrasena
    ) {}

    public record UsuarioResponse(
        Long id,
        String correo,
        Rol rol
    ) {}

    public record LoginRequest(
        String correo,
        String contrasena
    ) {}

    public record LoginResponse(
        String token,
        String tipo,
        Long id,
        String correo,
        Rol rol
    ) {}

    // POST: Registrar un cliente
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(
            @RequestBody RegistroRequest request) {

        Usuario usuario = usuarioService.registrarCliente(
            request.correo(),
            request.contrasena()
        );

        return new UsuarioResponse(
            usuario.getId(),
            usuario.getCorreo(),
            usuario.getRol()
        );
    }

    // POST: Iniciar sesion y obtener JWT
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        if (request == null ||
            request.correo() == null ||
            request.contrasena() == null) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Correo y contrasena obligatorios"
            );
        }

        String correo = request.correo()
                .trim()
                .toLowerCase(Locale.ROOT);

        // Verificar correo y contrasena con Spring Security
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    correo,
                    request.contrasena()
                )
            );
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Credenciales invalidas"
            );
        }

        // Buscar el usuario autenticado en MySQL
        Usuario usuario = usuarioRepository
                .findByCorreo(correo)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales invalidas"
                ));

        // Generar un JWT firmado
        String token = jwtService.generarToken(usuario);

        return new LoginResponse(
            token,
            "Bearer",
            usuario.getId(),
            usuario.getCorreo(),
            usuario.getRol()
        );
    }
}
