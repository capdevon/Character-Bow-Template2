package mygame.util;

import com.capdevon.anim.Animation3;

/**
 * 
 * @author capdevon
 */
public interface AnimDefs {
    
    final String ARCHER_ASSET_MODEL = "Models/Archer/archer.j3o";
    
    final Animation3 Idle           = new Animation3("Idle", true, .2f);
    final Animation3 Running        = new Animation3("Running", true);
    final Animation3 Running_2      = new Animation3("Running_2", true);
    final Animation3 Aim_Idle       = new Animation3("Aim_Idle", false);
    final Animation3 Aim_Overdraw   = new Animation3("Aim_Overdraw", false);
    final Animation3 Aim_Recoil     = new Animation3("Aim_Recoil", false);
    final Animation3 Draw_Arrow     = new Animation3("Draw_Arrow", false);
    
    final Animation3 Water_Idle     = new Animation3("Water_Idle", true);
    final Animation3 Water_Moving   = new Animation3("Water_Moving", true);
    final Animation3 Swimming       = new Animation3("Swimming", true);
}
