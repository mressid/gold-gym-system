package  com.BackEnd.Master.GYM.services.Impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import  com.BackEnd.Master.GYM.Exceptions.EntityNotFoundException;
import  com.BackEnd.Master.GYM.Exceptions.InvalidEntityException;

import  com.BackEnd.Master.GYM.entity.AppUsers;
import  com.BackEnd.Master.GYM.repository.AppUserRepo;
import  com.BackEnd.Master.GYM.services.AppUserService;
import  com.BackEnd.Master.GYM.util.UsernameNormalizer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService{

    private final AppUserRepo appUserRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AppUsers findById(Long id) {
        return appUserRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    @Override
    public List<AppUsers> findAll() {
        return appUserRepo.findAll();
    }
    
    @Override
    public AppUsers findByUserName(String userName) {
        return appUserRepo.findByUserName(userName);
    }

    @Override
    public List<AppUsers> findByRoleRoleName(String roleName) {
        return appUserRepo.findByRoleRoleName(roleName);
    }

    @Override
    public long count() {
        return appUserRepo.count();
    }

    @Override
    public long countByRoleRoleName(String roleName) {
        return appUserRepo.countByRoleRoleName(roleName);
    }

    @Override
    public List<AppUsers> searchUsers(String query) {
        return appUserRepo.findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelephoneContainingIgnoreCase(
                query, query, query);
    }

    @Override
    public AppUsers insert(AppUsers entity) {
        if (entity.getDisplayName() == null || entity.getDisplayName().isEmpty()) {
            throw new InvalidEntityException("Display name cannot be empty.");
        }
        entity.setUserName(generateUniqueUsername(UsernameNormalizer.normalize(entity.getDisplayName())));
        return appUserRepo.save(entity);
    }

    // Login handle is derived once at creation and never re-derived on later displayName edits,
    // so renaming someone's display name can't silently change what they log in with
    private String generateUniqueUsername(String base) {
        String candidate = base;
        int suffix = 2;
        while (appUserRepo.findByUserName(candidate) != null) {
            candidate = base + suffix;
            suffix++;
        }
        return candidate;
    }

    @Override
    public AppUsers update(AppUsers Entity) {
        AppUsers currentUser = appUserRepo.findById(Entity.getId())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

        currentUser.setDisplayName(Entity.getDisplayName());
        currentUser.setEmail(Entity.getEmail());
        currentUser.setTelephone(Entity.getTelephone());
        currentUser.setMotDePasse(Entity.getMotDePasse());

        return appUserRepo.save(currentUser);
    }

    @Override
    public AppUsers updatePassword(Long userId ,String password) {
        AppUsers currentUser = appUserRepo.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
        currentUser.setMotDePasse(passwordEncoder.encode(password));

        return appUserRepo.save(currentUser);
    }

    @Override
    public void deleteById(Long id) {
        appUserRepo.deleteById(id);
    }


}
