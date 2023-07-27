package com.myboard3.myboard3.controller;

import com.myboard3.myboard3.entity.Board;
import com.myboard3.myboard3.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BoardController {

    @Autowired
    BoardService boardService;

    @GetMapping("/board/write")
    public String boardwrite() {
        return "board/boardwrite";
    }

    @PostMapping("/board/writepro")
    public String boardwritepro(Board board) {

        if (board.getPassword() == "") {
            board.setPassword(null);
        } else {
            board.setPassword(board.getPassword());
        }

        boardService.write(board);
        return "redirect:/board/list";
    }

//    @GetMapping("board/list")
//    public String boardList(Model model) {
//        model.addAttribute("list", boardService.boardList());
//        return "/board/boardlist";
//    }

    @GetMapping("/board/list")
    public String boardllist(Model model,
                             @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
                             Pageable pageable,
                             String searchKeyword) {

        Page<Board> list;

        if (searchKeyword == null) {
            list = boardService.boardList(pageable);
        } else {
            list = boardService.boardSearchList(searchKeyword, pageable);
        }

        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "board/boardlist";
    }

    @GetMapping("/board/view") // localhost:8080/board/view?id=1
    public String boardView(@RequestParam("id") Integer id, Model model) {
        Board board = boardService.boardView(id);
        model.addAttribute("board", board);
        return "board/boardview";
    }

//    @GetMapping("/board/view")
//    public String boardView(@RequestParam("id") Integer id, Model model, @RequestParam(required = false) String password) {
//        Board board = boardService.boardView(id);
//
//        boolean passwordVerified = true; // 기본값은 true로 설정합니다.
//
//        if (board.getPassword() != null) {
//            // 비밀번호가 설정되어 있으면 입력된 비밀번호와 일치하는지 확인합니다.
//            passwordVerified = board.getPassword().equals(password);
//
//            if (!passwordVerified) {
//                // 비밀번호가 일치하지 않는 경우 비밀번호 입력 페이지로 이동합니다.
//                model.addAttribute("boardId", id);
//                return "board/boardpassword";
//            }
//        }
//
//        model.addAttribute("board", board);
//        model.addAttribute("passwordVerified", passwordVerified);
//
//        return "board/boardview";
//    }

    @GetMapping("/board/modify/{id}") // localhost:8080/board/modify/8
    public String boardModify(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("board", boardService.boardView(id));
        return "board/boardmodify";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board) {
        Board boardTemp = boardService.boardView(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        if (board.getPassword() == "") {
            boardTemp.setPassword(null);
        } else {
            boardTemp.setPassword(board.getPassword());
        }
        boardService.write(boardTemp);
        return "redirect:/board/view?id={id}";
    }

    @GetMapping("/board/delete")
    public String boardDelete(Integer id) {
        boardService.boardDelete(id);
        return "redirect:/board/list";
    }


    @GetMapping("/board/password")
    public String boardPassword(@RequestParam("id") Integer id, Model model) {
        model.addAttribute("boardId", id);
        return "board/boardpassword";
    }

    @PostMapping("/board/passwordpro")
    public String boardPasswordPro(@RequestParam("id") Integer id, @RequestParam("password") String password) {
        Board board = boardService.boardView(id);

        if (board.getPassword() != null && board.getPassword().equals(password)) {
            // 비밀번호가 일치하는 경우 해당 게시글의 상세 페이지로 이동
            return "redirect:/board/view?id=" + id;
        } else {
            // 비밀번호가 일치하지 않는 경우 비밀번호 입력 페이지로 다시 이동
            return "redirect:/board/password?id=" + id;
        }
    }
}
