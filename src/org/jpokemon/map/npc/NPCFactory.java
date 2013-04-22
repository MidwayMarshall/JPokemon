package org.jpokemon.map.npc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jpokemon.map.Requirement;

public class NPCFactory {
  public static Collection<NPC> build(int area) {
    Map<Integer, NPC> npcs = new HashMap<Integer, NPC>();

    // Get all in this area
    for (NPCActionSetInfo npcMapping : NPCActionSetInfo.get(area)) {

      // If this NPC isn't in the area yet, build it
      if (npcs.get(npcMapping.getNumber()) == null) {
        NPCInfo info = NPCInfo.get(npcMapping.getNumber());
        NPC npc = new NPC(info);
        npcs.put(npcMapping.getNumber(), npc);
      }

      ActionSet actionset = buildActionSet(npcMapping);
      npcs.get(npcMapping.getNumber()).addActionSet(actionset);

    }

    return npcs.values();
  }

  private static ActionSet buildActionSet(NPCActionSetInfo info) {
    ActionSet as = new ActionSet();

    for (NPCActionMapping action : NPCActionMapping.get(info.getNumber(), info.getActionset())) {
      Action a = new Action(action.getType(), action.getData());
      as.addAction(a);
    }

    as.setOption(info.getOption());
    as.requirements(buildActionSetRequirements(info.getNumber(), info.getActionset()));

    return as;
  }

  private static List<List<Requirement>> buildActionSetRequirements(int number, int set) {
    Map<Integer, List<Requirement>> requirementMaps = new HashMap<Integer, List<Requirement>>();

    for (NPCActionRequirement req : NPCActionRequirement.get(number, set)) {
      if (requirementMaps.get(req.getRequirementset()) == null)
        requirementMaps.put(req.getRequirementset(), new ArrayList<Requirement>());

      requirementMaps.get(req.getRequirementset()).add(new Requirement(req.getType(), req.getData()));
    }

    List<List<Requirement>> requirements = new ArrayList<List<Requirement>>();
    for (Map.Entry<Integer, List<Requirement>> reqList : requirementMaps.entrySet()) {
      requirements.add(reqList.getValue());
    }

    return requirements;
  }
}