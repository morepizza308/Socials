package de.joergmorbitzer.Socials.controller;

import de.joergmorbitzer.Socials.entities.Friendship;
import de.joergmorbitzer.Socials.entities.PrivacyTarget;
import de.joergmorbitzer.Socials.entities.SocialUser;
import de.joergmorbitzer.Socials.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@org.springframework.web.bind.annotation.RestController()
@RequestMapping("/api")
public class RestController {

    private final List<SocialUser> requested = new ArrayList<>();
    private final List<SocialUser> revoked = new ArrayList<>();
    private final List<SocialUser> denied = new ArrayList<>();
    private List<SocialUser> publicNonFriends;

    @Autowired
    UserRepository userRepo;

    @GetMapping("/users")
    public Map<Long, String> restUsers(Authentication auth)
    {
        SocialUser user = userRepo.findByUsername(auth.getName())
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
