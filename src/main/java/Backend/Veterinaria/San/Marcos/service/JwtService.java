
package Backend.Veterinaria.San.Marcos.service;

import Backend.Veterinaria.San.Marcos.model.Usuario;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generarToken(Usuario usuario) {

        Instant ahora = Instant.now();

        // Información que contendrá el token
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("veterinaria-san-marcos")
                .subject(usuario.getCorreo())
                .issuedAt(ahora)
                .expiresAt(ahora.plus(Duration.ofHours(1)))
                .claim("rol", usuario.getRol().name())
                .build();

        // Algoritmo de firma
        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        // Generar y firmar el JWT
        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}
