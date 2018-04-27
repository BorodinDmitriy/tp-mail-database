package com.database.db.controller;

import com.database.db.DAO.UserDAO;
import com.database.db.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/user")
public class UserController {
    private UserDAO uDAO;

    @Autowired
    UserController(UserDAO uDAO) {
        this.uDAO = uDAO;
    }

    @PostMapping(path = "/{nickname}/create")
    public ResponseEntity createUser(@PathVariable(name = "nickname") String nickname, @RequestBody User u) {
        try {
            u.setNickname(nickname);
            uDAO.insertUser(u);
            return ResponseEntity.status(HttpStatus.CREATED).body(u);
        } catch (DuplicateKeyException e) {
            List<User> result = uDAO.getUsers(u);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
    }

    @GetMapping(path = "/{nickname}/profile")
    public ResponseEntity getUserProfile(@PathVariable(name = "nickname") String nickname) {
        User u = uDAO.getUserByNickname(nickname);
        if (u == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String("No user with nickname " + nickname));
        return ResponseEntity.status(HttpStatus.OK).body(u);
    }

    @PostMapping(path = "/{nickname}/profile")
    public ResponseEntity updateUser(@PathVariable(name = "nickname") String nickname, @RequestBody User u) {
        try {
            u.setNickname(nickname);
            if (uDAO.getUserByNickname(nickname) == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String("No user with nickname " + nickname));
            uDAO.updateUser(u);
            return ResponseEntity.status(HttpStatus.OK).body(uDAO.getUserByNickname(nickname));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new String("Not unique email"));
        }
    }
}