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

    // Создание пользователя
    @PostMapping(path = "/{nickname}/create")
    public ResponseEntity createUser(@PathVariable(name = "nickname") String nickname, @RequestBody User u) {
        try {
            u.setNickName(nickname);
            uDAO.insertUser(u);
            return ResponseEntity.status(HttpStatus.CREATED).body(u);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(uDAO.getUsers(u));
        }
    }

    // Получение информации о пользователе
    @GetMapping(path = "/{nickname}/profile")
    public ResponseEntity getUserProfile(@PathVariable(name = "nickname") String nickname) {
        User u = uDAO.getUserByNick(nickname);
        if (u == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String("No user with nickname " + nickname));
        return ResponseEntity.status(HttpStatus.OK).body(u);
    }

    // Обновление информации о пользователе
    @PostMapping(path = "/{nickname}/profile")
    public ResponseEntity updateUser(@PathVariable(name = "nickname") String nickname, @RequestBody User u) {
        try {
            u.setNickName(nickname);
            if (uDAO.getUserByNick(nickname) == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String("No user with nickname " + nickname));
            uDAO.updateUser(u);

            return ResponseEntity.status(HttpStatus.OK).body(uDAO.getUserByNick(nickname));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new String("Not unique email"));
        }
    }
}
