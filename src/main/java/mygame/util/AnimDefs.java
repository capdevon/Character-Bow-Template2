package mygame.util;

import com.capdevon.anim.Animation3;

/**
 *
 * @author capdevon
 */
public interface AnimDefs {

    String ARCHER_ASSET_MODEL = "Models/Archer/YBot.j3o";

    Animation3 Idle                   = new Animation3("Idle", true);
    Animation3 Idle2                  = new Animation3("Idle_2", true);
    Animation3 Running                = new Animation3("Running", true);
    Animation3 Sprinting              = new Animation3("Sprinting", true);
    Animation3 StandingIdle           = new Animation3("StandingIdle", true, .2f);
    Animation3 StandingAimIdle        = new Animation3("StandingAimIdle", false);
    Animation3 StandingAimOverdraw    = new Animation3("StandingAimOverdraw", false);
    Animation3 StandingAimRecoil      = new Animation3("StandingAimRecoil", false);
    Animation3 StandingDrawArrow      = new Animation3("StandingDrawArrow", false);

}
