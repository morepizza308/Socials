package de.joergmorbitzer.Socials.controller;

import de.joergmorbitzer.Socials.entities.*;
import de.joergmorbitzer.Socials.repositories.*;
import de.joergmorbitzer.Socials.security.MyUserDetailsService;
import de.joergmorbitzer.Socials.security.SocialPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class WebController {

    private final AuthenticationManager authManager;

    public WebController(AuthenticationManager authManager)
    {
        this.authManager = authManager;
    }
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private FriendRepository friendRepo;
    @Autowired
    private UpdateRepository updateRepository;
    @Autowired
    private GroupRepository groupRepo;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MyUserDetailsService userDetailsService;

    GrantedAuthority admin = new SimpleGrantedAuthority("ROLE_ADMIN");

    @GetMapping("/")
    public String startseiteZeigen(Model model, Authentication auth)
    {
        if (auth != null) {
            model.addAttribute("isLoggedIn", auth.isAuthenticated());
            model.addAttribute("isAdmin", auth.getAuthorities().contains(admin));
        }
        model.addAttribute("userOnline", userRepo.findByIsOnline(true));
        return "index";
    }

    @GetMapping("/updates")
    public String showUpdates(Model model, Authentication auth)
    {

        UpdateTarget[] targets = { UpdateTarget.GLOBAL, UpdateTarget.FRIENDS, UpdateTarget.SELECT };
        SocialUser user = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<SocialUser> friends = freundeFinden(user.getUid());
        friends.add(user);

        List<SocialUpdate> allUpdates = updateRepository.findByTargetOrAuthorIn(UpdateTarget.GLOBAL, friends);
        List<SocialUpdate> sortedUpdates = updateRepository.findByTargetOrAuthorIn(UpdateTarget.GLOBAL, friends).stream().sorted(new DateComparator()).toList();
        model.addAttribute("user", user);
        model.addAttribute("isLoggedIn", auth.isAuthenticated());
        model.addAttribute("isAdmin", auth.getAuthorities().contains(admin));
        model.addAttribute("allUpdates", allUpdates);
        model.addAttribute("sortedUpdates", sortedUpdates);
        model.addAttribute("targets", targets);
        model.addAttribute("newupdate", new SocialUpdate());
        return "updates/updates";
    }

    @PostMapping("/updates/new-update")
    public String newUpdate(@ModelAttribute SocialUpdate newupdate, Model model,
                            Authentication auth)
    {
        SocialUser user = userRepo.findByUsername(auth.getName())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        newupdate.setCreated(new Date(System.currentTimeMillis()));
        newupdate.setAuthor(user);
        updateRepository.save(newupdate);
        model.addAttribute("isLoggedIn", auth.isAuthenticated());
        model.addAttribute("isAdmin", auth.getAuthorities().contains(admin));
        model.addAttribute("newupdate", new SocialUpdate());
        return "redirect:/updates";
    }
    @GetMapping("/users")
    public String userZeigen(Model model, Authentication auth)
    {
        model.addAttribute("isLoggedIn", auth.isAuthenticated());
        model.addAttribute("isAdmin", auth.getAuthorities().contains(admin));
        if (auth.isAuthenticated())
        {
            SocialUser user = userRepo.findByUsername(auth.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            List<SocialUser> friends = freundeFinden(user.getUid());
            model.addAttribute("freunde", friends);
            List<SocialUser> pending = anfragenFinden(user.getUid());
            model.addAttribute("pending", pending);
            List<SocialUser> userFiltered = userRepo.findByUidNot(user.getUid());
            model.addAttribute("filteredlist", userFiltered);
        }
        List<SocialUser> alleUser = userRepo.findAll();
        model.addAttribute("alleUser", alleUser);
        return "users/users";
    }

    @GetMapping("/users/{uid}")
    public String profilZeigen(@PathVariable Long uid, Model model, Authentication auth)
    {
        SocialUser request = userRepo.findByUid(uid)
                        .orElseThrow(() -> new IllegalArgumentException("ID nicht vorhanden"));
        List<SocialUpdate> alleUpdates = updateRepository.findByAuthor(request);
        model.addAttribute("alleUpdates", alleUpdates);
        model.addAttribute("isAdmin", auth.getAuthorities().contains(admin));
        model.addAttribute("userreq", request);
        return "users/profile";
    }

    @GetMapping("/groups")
    public String groupZeigen(Model model, Authentication auth)
    {
        List<SocialGroup> alleGruppen = groupRepo.findAll();
        if (auth != null) {
            model.addAttribute("isLoggedIn", auth.isAuthenticated());
            model.addAttribute("isAdmin", auth.getAuthorities().contains(admin));
        }
        model.addAttribute("alleGruppen", alleGruppen);
        return "groups/groups";
    }
    @GetMapping("/groups/{groupId}")
    public String showGroup(@PathVariable Long groupId, Model model, Authentication auth)
    {
        SocialUser user = userRepo
                .findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        SocialPrincipal eingeloggt = new SocialPrincipal(user);
        SocialGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        boolean loggedInIsFounder = group.getFoundedBy() == user;

        model.addAttribute("isMember", group.getMembers().contains(user));
        model.addAttribute("groupUpdates", group.getGroupUpdates());
        model.addAttribute("group", group);
        model.addAttribute("isPrivate", group.isPrivate());
        model.addAttribute("eingeloggt", eingeloggt);
        model.addAttribute("isLoggedIn", auth.isAuthenticated());
        model.addAttribute("isAdmin", auth.getAuthorities().contains(admin));
        model.addAttribute("loggedInIsFounder", loggedInIsFounder);

        return "groups/show-group";
    }
    @GetMapping("/groups/join/{groupId}")
    public String joinGroup(@PathVariable Long groupId, Model model, Authentication auth)
    {
        SocialUser user = userRepo
                .findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        SocialGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        Set<SocialGroup> memberOf = user.getMemberOf();
        model.addAttribute("isAdmin", auth.getAuthorities().contains(admin));
        memberOf.add(group);
        user.setMemberOf(memberOf);
        userRepo.save(user);

        return "redirect:/";
    }
    @GetMapping("/groups/leave/{groupId}")
    public String leaveGroup(@PathVariable Long groupId, Model model, Authentication auth)
    {
        SocialUser user = userRepo
                .findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        SocialGroup leftGroup = groupRepo.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        if (!leftGroup.getMembers().contains(user))
            return "error";
        model.addAttribute("user", user);
        model.addAttribute("gruppe", leftGroup);
        model.addAttribute("isAdmin", auth.getAuthorities().contains(admin));

        return "groups/leave-group";
    }
    @PostMapping("/groups/leave/")
    public String doLeaveGroup(@RequestParam("groupId") Long groupId, Model model, Authentication auth)
    {
        SocialUser user = userRepo
                .findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        SocialGroup leftGroup = groupRepo.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        if (!leftGroup.getMembers().contains(user))
            return "error";
        System.out.println(user.getMemberOf());
        System.out.println(leftGroup.getMembers());
        user.removeMembership(leftGroup);
        userRepo.saveAndFlush(user);
        System.out.println(user.getMemberOf());
        System.out.println(leftGroup.getMembers());
        return "groups/groups";
    }
    @GetMapping("/groups/edit/{groupId}")
    public String editGroup(@PathVariable Long groupId, Model model, Authentication auth)
    {
        SocialGroup group;
        if (groupRepo.existsById(groupId)) {
            group = groupRepo.findById(groupId).get();
            model.addAttribute("group", group);
            model.addAttribute("isAdmin", auth.getAuthorities().contains(admin));
            return "groups/new-group";
        }
        else
            return "forbidden";
    }

    @GetMapping("/groups/new-group")
    public String neueGruppeZeigen(Model model)
    {
        return "groups/new-group";
    }

    @GetMapping("/messages")
    public String nachrichtenZeigen(Model model, Authentication auth)
    {
        List<SocialUser> alleAnschreibbarenUser;
        SocialUser user = userRepo
                .findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        model.addAttribute("isAdmin", auth.getAuthorities().contains(admin));

        // ADMIN darf jedem eine NAchricht schicken,
        if (auth.getAuthorities().contains(admin))
        {
            alleAnschreibbarenUser = userRepo.findAll();
        }
        // User dürfen Freunden und ADMINS schreiben
        else {
            alleAnschreibbarenUser = freundeFinden(user.getUid());
            List<SocialUser> admins = userRepo.findByRole("ROLE_ADMIN");
            alleAnschreibbarenUser.addAll(admins);
        }


        SocialPrincipal eingeloggt = new SocialPrincipal(user);

        List<Message> privMessages = messageRepository.findByTouserUid(user.getUid());

        model.addAttribute("isLoggedIn", auth.isAuthenticated());
        model.addAttribute("senderuid", eingeloggt.getUid());
        model.addAttribute("messages", privMessages);
        model.addAttribute("alleUser", alleAnschreibbarenUser);
        return "messaging/messages";
    }
    @GetMapping("/messages/new")
    public String neueNachricht(Model model, Authentication auth)
    {
        List<SocialUser> alleUser = userRepo.findAll();

        model.addAttribute("isLoggedIn", auth.isAuthenticated());
        model.addAttribute("isAdmin", auth.getAuthorities().contains(admin));
        model.addAttribute("newMessage", new Message());
        model.addAttribute("alleUser", alleUser);

        return "messaging/new-message";
    }
    @PostMapping("/messages/send")
    public String sendeNachricht(@RequestParam("heading") String heading,
                                 @RequestParam("message") String message,
                                 @RequestParam("touser") Long touser,
                                 @RequestParam("fromuser") Long fromuser)
    {
        Message newMessage = new Message();
        newMessage.setHeading(heading);
        newMessage.setMessage(message);
        newMessage.setTouser(userRepo.findById(touser).get());
        newMessage.setFromuser(userRepo.findById(fromuser).get());
        messageRepository.save(newMessage);
        System.out.println("Soweit läufts");
        return "messaging/messages";
    }
    @GetMapping("/messages/{mid}")
    public String liesNachricht(@PathVariable Long mid,  Model model,
                                Authentication auth)
    {
        if (!messageRepository.existsById(mid))
            return "error";
        Message thisMessage = messageRepository.findByMid(mid);
        SocialUser eingeloggt = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!thisMessage.getTouser().equals(eingeloggt))
            return "error";
        model.addAttribute("thisMessage", thisMessage);
        model.addAttribute("isAdmin", auth.getAuthorities().contains(admin));
        return "messaging/read-message";
    }

    @GetMapping("/backend")
    public String backendLaden(Model model, Authentication auth)
    {
        return "backend/index";
    }

    protected List<SocialUser> freundeFinden(long uid)
    {
        List<Friendship> suche = friendRepo.findByRequestUidOrFriendOfUid(uid, uid);
        suche.retainAll(friendRepo.findByAccepted(true));
        List<SocialUser> freunde = new ArrayList<>();
        for (Friendship f : suche)
        {
            if (f.getRequestUid() == uid)
                freunde.add(userRepo.findByUid(f.getFriendOfUid()).get());
            else
                freunde.add(userRepo.findByUid(f.getRequestUid()).get());
        }
        return freunde;
    }

    protected List<SocialUser> anfragenFinden(long uid)
    {
        List<Friendship> suche = friendRepo.findByRequestUidOrFriendOfUid(uid, uid);
        suche.retainAll(friendRepo.findByAccepted(false));
        List<SocialUser> anfrage = new ArrayList<>();
        for (Friendship f : suche)
        {
            if (f.getRequestUid() == uid)
                anfrage.add(userRepo.findByUid(f.getFriendOfUid()).get());
            else
                anfrage.add(userRepo.findByUid(f.getRequestUid()).get());
        }
        return anfrage;
    }
}

class DateComparator implements Comparator<SocialUpdate> {
    @Override
    public int compare(SocialUpdate s1, SocialUpdate s2) {
        return s2.getCreated().compareTo(s1.getCreated());
    }
}