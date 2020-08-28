import PlayerNotFoundException from "./PlayerNotFoundException";
import GameDatabase from "./GameDatabase";
import sleep from "../../utils/Sleep";

export default class EntityManager {
	private readonly gdb = new GameDatabase("game.db");

	getEntityByID(entity_id: number) {
		throw new Error("not implemented");
	}

	async getPlayerByID(platform_id: string, platform: string) {
		console.debug("EntityManager.getPlayerByID() start");
		const player = await this.gdb.selectPlayer(platform_id, platform);
		if (player === []){
			throw new PlayerNotFoundException();
		}
		console.debug("EntityManager.getPlayerByID()", player);
		await sleep(100);
		throw new Error("not implemented");
	}

	async createPlayer(platform_id: string, platform: string){
		this.gdb.insertPlayer(platform_id, platform, null);
	}
}