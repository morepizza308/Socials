package de.joergmorbitzer.Socials.controller;

import de.joergmorbitzer.Socials.entities.Friendship;
import de.joergmorbitzer.Socials.entities.PrivacyTarget;
import de.joergmorbitzer.Socials.entities.SocialUser;
import de.joergmorbitzer.Socials.repositories.UserRepository;
import de.joergmorbitzer.Socials.security.JwtRequest;
import de.joergmorbitzer.Socials.security.JwtResponse;
import de.joergmorbitzer.Socials.security.JwtTokenUtil;
import de.joergmorbitzer.Socials.security.SocialPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api")
public class RestController {

    private final List<SocialUser> requested = new ArrayList<>();
    private final List<SocialUser> revoked = new ArrayList<>();
    private final List<SocialUser> denied = new ArrayList<>();
    private List<SocialUser> publicNonFriends;

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private JwtTokenUtil tokenUtil;
    @Autowired
    private AuthenticationManager authManager;

    @GetMapping("/authenticate")
    public ResponseEntity<String> nurTest(Authentication auth)
    {
        if (auth != null)
            return ResponseEntity.ok("Das ist ok" + auth.getName());
        else
            return ResponseEntity.ok("Auch ok");
    }
    @PostMapping("/control")
    public ResponseEntity<?> controlToken(@RequestParam String token)
    {
        System.out.println(tokenUtil.validateToken(token));
        return ResponseEntity.ok("Stimmt");
    }
    @PostMapping("/subject")
    public ResponseEntity<?> parseSubject(@RequestParam String token)
    {
        System.out.println(tokenUtil.getUserNameFromToken(token));
        return ResponseEntity.ok("Gut");
    }
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthToken(@RequestParam String username,
                                             @RequestParam String password) throws Exception
    {
        SocialUser user;
        if (userRepo.findByUsername(username).isPresent())
            user = userRepo.findByUsername(username).get();
        else
            throw new UsernameNotFoundException("User not found");

        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
        SocialPrincipal thisUser = (SocialPrincipal) auth.getPrincipal();

        User target = new User(thisUser.getUsername(), thisUser.getPassword(), thisUser.getAuthorities());
        final String token = tokenUtil.generateToken(target);
        return ResponseEntity.ok(new JwtResponse(token));
    }
    @PostMapping("/users")
    public Map<Long, String> restUsers(@RequestParam String token)
    {
        String username = tokenUtil.getUserNameFromToken(token);
        SocialUser user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        HashMap<Long, String> allUsers = new HashMap<>();
        List<SocialUser> loadUsers = userRepo.findAll()
                .stream()
                .filter(u -> u.getPrivacy().equals(PrivacyTarget.GLOBAL))
                .toList();

        for (SocialUser s : loadUsers)
        {
            allUsers.put(s.getUid(), s.getUsername());
        }
        return allUsers;
    }

    @GetMapping("/friends")
    public Map<Long, String> restFriends(Authentication auth)
    {
        SocialUser user = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Set<SocialUser> friends = user.getFriends();
        Map<Long, String> friendlist = new HashMap<>();
        for (SocialUser s : friends)
        {
            friendlist.put(s.getUid(), s.getUsername());
        }
        return friendlist;
    }

    private void filteredFriendship(Friendship fs, SocialUser user, List<SocialUser> target)
    {

    }
}
