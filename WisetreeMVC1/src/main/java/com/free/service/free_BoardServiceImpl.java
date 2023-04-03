package com.free.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.free.mapper.free_BoardMapper;
import com.free.model.free_BoardVO;
import com.free.model.free_PagingVO;

@Service("free_boardServiceImpl")
public class free_BoardServiceImpl implements free_BoardService {
	
	@Autowired
	private free_BoardMapper free_boardMapper;
	
	//게시판 글 작성
	@Override
	public int insertBoard(free_BoardVO board) {
		return this.free_boardMapper.insertBoard(board);
	}

	//글번호 순서가 아닌 최근글 순서로 표현
//	@Override
//	public List<free_BoardVO> selectBoardAll(Map<String, Integer> map) {
//		// TODO Auto-generated method stub
//		return this.free_boardMapper.selectBoardAll(map);
//	}
	@Override
	public List<free_BoardVO> selectBoardAll(Map<String,Integer> map) {
		// TODO Auto-generated method stub
		return this.free_boardMapper.selectBoardAll(map);
	}
	
	//검색 조건에 따라 페이지에 순서대로 출력
	@Override
	public List<free_BoardVO> selectBoardAllPaging(free_PagingVO paging) {
		// TODO Auto-generated method stub
		return this.free_boardMapper.selectBoardAllPaging(paging);
	}
	
	//게시글의 총 개수 출력
	@Override
	public int getTotalCount(free_PagingVO paging) {
		// TODO Auto-generated method stub
		return this.free_boardMapper.getTotalCount(paging);
	}

	//해당 게시글 정보 불러오기 (View페이지)
	@Override
	public free_BoardVO selectBoardByIdx(Integer num) {
		// TODO Auto-generated method stub
		return this.free_boardMapper.selectBoardByIdx(num);
	}
	
	//조회수 증가 기능
	@Override
	public int updateReadnum(Integer idx) {
		// TODO Auto-generated method stub
		return this.free_boardMapper.updateReadnum(idx);
	}

	//해당 게시글 삭제 기능
	@Override
	public void deleteBoard(Integer num) {
		// TODO Auto-generated method stub
		 this.free_boardMapper.deleteBoard(num);
	}

	//해당 게시글 수정 기능
	@Override
	public int updateBoard(free_BoardVO Free_Board) {
		return this.free_boardMapper.updateBoard(Free_Board);
	}

	//답변 글쓰기 기능
	@Override
	public int rewriteBoard(free_BoardVO Free_Board) {

		free_BoardVO parent = this.selectRefLevSunbun(Free_Board.getNum());
		
		int n = this.updateSunbun(parent);
		
		//해당 글의 refer, lev, sunbun 수정 (답변글쓰기 위해)
		Free_Board.setRefer(parent.getRefer());
		Free_Board.setLev(parent.getLev() + 1);
		Free_Board.setSunbun(parent.getSunbun() + 1);
		
		return this.free_boardMapper.rewriteBoard(Free_Board);
	}

	//답변을 달 게시물 정보 가져오기
	@Override
	public free_BoardVO selectRefLevSunbun(int idx) {
		// TODO Auto-generated method stub
		return this.free_boardMapper.selectRefLevSunbun(idx);
	}

	//답변을 달 게시물의 순번에 1을 추가함으로써 해당 게시글 아래에 답변이 달리게 하는 기능
	@Override
	public int updateSunbun(free_BoardVO parent) {
		// TODO Auto-generated method stub
		return this.free_boardMapper.updateSunbun(parent);
	}

	@Override
	public List<free_BoardVO> findBoard(free_PagingVO paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTotalCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String selectPwd(Integer idx) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
