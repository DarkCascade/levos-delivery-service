package com.DarkCascade.Levo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class GridLocation {
	private Sprite _sprite;
	private int _rowNo;
	private int _colNo;
	
	public void Initialize(Sprite s, int row, int col) {
		_sprite = s;
		_rowNo = row;
		_colNo = col;
	}
	
	public void set_SpriteTexture(Texture t) {
		_sprite.setTexture(t);
	}
	
	public Sprite get_Sprite() {
		return _sprite;
	}
	
	public int get_RowNo() {
		return _rowNo;
	}

	public int get_ColNo() {
		return _colNo;
	}	
}
