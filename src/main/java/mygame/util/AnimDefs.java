package mygame.util;

import com.capdevon.anim.Animation3;

/**
 *
 * @author capdevon
 */
public interface AnimDefs {

    final String ARCHER_ASSET_MODEL = "Models/Archer/YBot.j3o";

    final Animation3 Idle                   = new Animation3("Idle", true);
    final Animation3 Idle2                  = new Animation3("Idle_2", true);
    final Animation3 Running                = new Animation3("Running", true);
    final Animation3 Sprinting              = new Animation3("Sprinting", true);
    final Animation3 StandingIdle           = new Animation3("StandingIdle", true, .2f);
    final Animation3 StandingAimIdle        = new Animation3("StandingAimIdle", false);
    final Animation3 StandingAimOverdraw    = new Animation3("StandingAimOverdraw", false);
    final Animation3 StandingAimRecoil      = new Animation3("StandingAimRecoil", false);
    final Animation3 StandingDrawArrow      = new Animation3("StandingDrawArrow", false);

}
