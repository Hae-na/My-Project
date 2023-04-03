package com.wisetree.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.common.CommonUtil;
import com.free.model.free_BoardVO;
import com.free.model.free_PagingVO;
import com.free.service.free_BoardService;

import lombok.extern.log4j.Log4j;

@Controller
@RequestMapping("/free_board")
@PropertySource("classpath:config/props/filed.properties")
@Log4j
public class BoardController {
	
	@Value("${file.dir}")
	private String upDir;
	
	@Autowired
	private free_BoardService boardService;
	
	
	@GetMapping("/write")
	public String boardForm() {
		
		return "free_board/free_boardWrite";
	}
	
	@Inject
	private CommonUtil util;
	
	
//글쓰기 기능 관련
	@PostMapping("/write")
	public String saveFile(HttpServletRequest req,@ModelAttribute("board") free_BoardVO board,BindingResult b) throws ServletException,IOException
	{	
		ServletContext app=req.getServletContext();
		b.getFieldError();
		System.out.println("asdasdadasdassda");
		log.info("board=="+board);


		log.info("req=="+req);
		
//		String upFile=req.getParameter("file1");
//		log.info(upFile);
		
		Collection<Part> parts=req.getParts();
		log.info("parts="+parts);
		for(Part p:parts) {
			log.info("===");
			//log.info("name={}",p.getName());
			
			
			Collection<String> headerNames=p.getHeaderNames();
			for(String headerName:headerNames) {
				log.info(headerName+p.getHeader(headerName));
			}
			//편의 메서드 
			//content-disposition, filename
			
			//데이터 읽기
			InputStream inputstream=p.getInputStream();
			String body=StreamUtils.copyToString(inputstream, StandardCharsets.UTF_8);
			log.info("body={}"+body);
			
			//파일 저장하기 
			if(StringUtils.hasText(p.getSubmittedFileName())){
				 String savePath=app.getRealPath(upDir)+p.getSubmittedFileName();
				 log.info("저장경로={}"+savePath);
				 
				 String tempname=p.getSubmittedFileName();
					log.info("파일명={}"+p.getSubmittedFileName());
					log.info("size"+p.getSize());
				board.setFilename(tempname);
				p.write(savePath);
			}
		}
		
		int n = boardService.insertBoard(board);
		log.info("n((((((((((((((((((((((((((((((((((("+n);
		
		
		return "redirect:list";
	}

	
	//리스트 기능 관련
	@GetMapping("/list")
	public String boardListPaging(Model m, @ModelAttribute("page") free_PagingVO page, HttpServletRequest req,
			@RequestHeader("User-Agent") String userAgent) {
		String myctx = req.getContextPath();
		
		HttpSession ses = req.getSession();
		
//		log.info("1 page == " + page);
		//게시글 총 개수 가져오기 및 블록 크기 설정
		int totlaCount = this.boardService.getTotalCount(page);
		page.setTotalCount(totlaCount);
		page.setPagingBlock(5);
		
		page.init(ses);
		
//		log.info("2.page == " +page);
		//페이지에 게시글 보여주기
		List<free_BoardVO> boardArr = this.boardService.selectBoardAllPaging(page);
		
		String loc = "free_board/list";
		String pageNavi = page.getPageNavi(myctx, loc, userAgent);
		
		m.addAttribute("pageNavi", pageNavi);
		m.addAttribute("paging", page);
		m.addAttribute("boardArr", boardArr);
		
		return "free_board/free_boardList2";
	}
	
	//해당 게시글 보기
	@GetMapping("/view/{num}")
	public String boardView(Model m, @PathVariable("num") int num) {
//		log.info("num===" + num);
		//해당 게시글 열면 조회수 증가
		int n = this.boardService.updateReadnum(num);
		
		//해당 게시글 열기
		free_BoardVO board = this.boardService.selectBoardByIdx(num);
		m.addAttribute("board", board);
		
		return "free_board/free_boardView";
	}
	
	
	//해당 게시글 삭제
	
	@GetMapping("/delete/{num}")
	public String deleteResult(@PathVariable("num") int num) {
		boardService.deleteBoard(num);
		return "redirect:/free_board/list";
	}
	
	
	@GetMapping("/view/edit/{num}")
	public String modifyForm(Model model,@PathVariable("num") int num) {
		free_BoardVO Free_Board=boardService.selectBoardByIdx(num);
		model.addAttribute("board",Free_Board);
		return "free_board/free_boardEdit";
	}
	
	//해당 게시글 수정
	@PostMapping("/view/edit/write")
	public String modify(Model m,@RequestParam("num") int num,@ModelAttribute free_BoardVO Free_Board) {
		int a=boardService.updateBoard(Free_Board);
		log.info("a<><><<><><><><<><>><"+a);
		
		return "redirect:/free_board/list";
		
	}


	
	
	
	
	//답변 글쓰기 기능
	@PostMapping("/rewrite")
	public String boardRewrite(Model m, @ModelAttribute free_BoardVO vo) {
//		log.info("vo == " + vo);
		m.addAttribute("num", vo.getNum());
		m.addAttribute("subject", vo.getSubject());
		
		return "free_board/free_boardRewrite";
	}
	
}
