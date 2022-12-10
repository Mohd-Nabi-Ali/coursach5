package com.pack.Services;

import com.pack.Repositories.RoleRepository;
import com.pack.Repositories.UserRepository;
import com.pack.Entities.Role;
import com.pack.Entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("Пользователь не найден");
        return user;
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public String saveUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return "Этот логин занят";
        }
        if(userRepository.findByEmail(user.getEmail())!=null){
            return "Этот email занят";
        }
        user.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "";
    }

    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public List<User> userList(Long atrId) {
        return em.createQuery("SELECT u FROM User u WHERE u.id = :paramId", User.class)
                .setParameter("paramId", atrId).getResultList();
    }
}
