package be.ucll.unit.repository;

import be.ucll.model.Profile;
import be.ucll.model.User;
import be.ucll.repository.UserRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserRepositoryStub implements UserRepository {


    private List<User> users = new ArrayList<>(List.of(
            new User("21Savage", 25, "21.savage@ucll.be", "john1234"),
            new User("Jane Toe", 30, "jane.toe@ucll.be", "jane1234"),
            new User("Jack Doe", 5, "jack.doe@ucll.be", "jack1234"),
            new User("Sarah Doe", 4, "sarah.doe@ucll.be", "sarah1234"),
            new User("Birgit Doe", 18, "birgit.doe@ucll.be", "birgit1234"),
            new User("22Savage", 25, "22.savage@ucll.be", "john1234", new Profile(
                    "Aspiring rapper with a love for tech.",
                    "Antwerp",
                    "Music, rapping, Basketball"
            )),
            new User("23Savage", 25, "23.savage@ucll.be", "john1234", new Profile(
                    "Aspiring rapper with a love for activities.",
                    "Brussels",
                    "Smoking, rapping, Coding"
            ))
    ));

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public <S extends User> S save(S entity) {
        users.add(entity);
        return entity;
    }

    @Override
    public void delete(User entity) {
        users.remove(entity);
    }
    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {}

    @Override
    public User findUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public boolean existsUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<User> findUsersByAgeBetween(int min, int max) {
        List<User> end = new ArrayList<>();
        for (User user : users){
            if (user.getAge() >= min && user.getAge() <= max ){
                end.add(user);
            }
        }
        return end;
    }

    @Override
    public List<User> findUserByName(String name) {
        return List.of();    }

    @Override
    public List<User> findUsersByName(String name) {
        List<User> end = new ArrayList<>();

        for (User user : users){
            if (user.getName().contains(name)){
                end.add(user);
            }
        }
        if (name.isEmpty()){
            end = users;
        }
        return end;
    }

    @Override
    public User findFirstByOrderByAgeDesc() {
        User oldest = users.get(0);
        for(User user : users){
            if(user.getAge() > oldest.getAge()){
                oldest = user;
            }
        }
        return oldest;
    }

    // story 24
    @Override
    public List<User> findUsersByProfile_InterestsContainingIgnoreCase(String interests) {
        if (interests == null || interests.isBlank()) return List.of();

        String interestLower = interests.toLowerCase();
        List<User> matchingUsers = new ArrayList<>();

        for (User user : users) {
            if (user.getProfile() == null) continue;
            List<String> userInterests = Collections.singletonList(user.getProfile().getInterests());
            if (userInterests == null) continue;

            for (String interest : userInterests) {
                if (interest != null && interest.toLowerCase().contains(interestLower)) {
                    matchingUsers.add(user);
                    break; // match found, no need to check other interests
                }
            }
        }
        return matchingUsers;
    }

    // Story 25
    @Override
    public List<User> findUsersByProfile_InterestsContainingIgnoreCaseAndAgeGreaterThanEqualOrderByProfile_Location(String interests, int age) {
        if (interests == null || interests.isBlank()) return List.of();

        String interestLower = interests.toLowerCase();
        List<User> matchingUsers = new ArrayList<>();

        for (User user : users) {
            if (user.getAge() < age) continue;
            if (user.getProfile() == null) continue;

            String userInterest = user.getProfile().getInterests();
            if (userInterest != null && userInterest.toLowerCase().contains(interestLower)) {
                matchingUsers.add(user);
            }
        }

        matchingUsers.sort(Comparator.comparing(u -> u.getProfile().getLocation(), String.CASE_INSENSITIVE_ORDER));
        return matchingUsers;
    }






    @Override public Optional<User> findById(Long aLong) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean existsById(Long aLong) {
        throw new UnsupportedOperationException();
    }

    @Override public long count() {
        throw new UnsupportedOperationException();
    }

    @Override public void deleteById(Long aLong) {
        throw new UnsupportedOperationException();
    }

    @Override public void deleteAll() {
        users.clear();
    }

    @Override public <S extends User> List<S> saveAll(Iterable<S> entities) {
        entities.forEach(users::add);
        return (List<S>) users;
    }

    @Override public List<User> findAllById(Iterable<Long> longs) {
        throw new UnsupportedOperationException();
    }

    @Override public void deleteAll(Iterable<? extends User> entities) {
        entities.forEach(users::remove);
    }

    @Override
    public List<User> findUsersByAgeAfter(int i) {
        return List.of();
    }


    @Override
    public void deleteUserByEmail(String email) {

    }



    @Override
    public void flush() {

    }

    @Override
    public <S extends User> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<User> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public User getOne(Long aLong) {
        return null;
    }

    @Override
    public User getById(Long aLong) {
        return null;
    }

    @Override
    public User getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends User> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends User> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends User, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<User> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return null;
    }
}
