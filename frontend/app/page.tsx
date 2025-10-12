"use client";

import { useEffect, useState } from "react";

interface Tile {
  word: string;
  role: "RED" | "BLUE" | "NEUTRAL" | "ASSASSIN";
  revealed: boolean;
}

interface Game {
  board: Tile[][];
  turn: "RED" | "BLUE";
}

export default function Home() {
  const [game, setGame] = useState<Game | null>(null);
  const [isSpymasterView, setIsSpymasterView] = useState(false);

  useEffect(() => {
    fetch("http://localhost:8080/api/game")
      .then((response) => {
        if (response.ok) {
          return response.json();
        }
        return null;
      })
      .then((data: Game | null) => setGame(data))
      .catch((error) => console.error("Error fetching game:", error));
  }, []);

  const createNewGame = () => {
    fetch("http://localhost:8080/api/game/new", {
      method: "POST",
    })
      .then((response) => response.json())
      .then((data: Game) => {
        setGame(data);
        setIsSpymasterView(false);
      })
      .catch((error) => console.error("Error creating new game:", error));
  };

  const handleTileClick = (row: number, col: number) => {
    fetch("http://localhost:8080/api/game/reveal", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ row, col }),
    })
      .then((response) => response.json())
      .then((data: Game) => setGame(data))
      .catch((error) => console.error("Error revealing tile:", error));
  };

  const getTileStyling = (tile: Tile) => {
    if (tile.revealed) {
      switch (tile.role) {
        case "RED":
          return "bg-red-500 text-white";
        case "BLUE":
          return "bg-blue-500 text-white";
        case "NEUTRAL":
          return "bg-yellow-200 text-white";
        case "ASSASSIN":
          return "bg-black text-white";
        default:
          return "bg-gray-200 text-black";
      }
    }

    let textColor = "text-black";
    if (isSpymasterView) {
      switch (tile.role) {
        case "RED":
          textColor = "text-red-500";
          break;
        case "BLUE":
          textColor = "text-blue-500";
          break;
        case "NEUTRAL":
          textColor = "text-yellow-600";
          break;
        case "ASSASSIN":
          textColor = "text-black";
          break;
        default:
          textColor = "text-black";
      }
    }
    return `bg-gray-200 ${textColor}`;
  };

  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-8">
      <div className="flex flex-col items-center">
        <h1 className="text-4xl font-bold mb-8">Codenames</h1>
        <button
          onClick={createNewGame}
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded mb-8"
        >
          New Game
        </button>
        {game && (
          <>
            <div className="grid grid-cols-5 gap-4">
              {game.board.map((row, rowIndex) =>
                row.map((tile, colIndex) => (
                  <div
                    key={`${rowIndex}-${colIndex}`}
                    onClick={() => !isSpymasterView && handleTileClick(rowIndex, colIndex)}
                    className={`w-40 h-28 rounded-md flex items-center justify-center text-center p-2 uppercase text-lg font-bold ${isSpymasterView ? 'cursor-not-allowed' : 'cursor-pointer'} ${getTileStyling(
                      tile
                    )}`}
                  >
                    {tile.word}
                  </div>
                ))
              )}
            </div>
            <div className="mt-8 flex gap-4">
              <button
                onClick={() => setIsSpymasterView(false)}
                className={`bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded ${!isSpymasterView ? 'border-4 border-red-500' : ''}`}
              >
                Player
              </button>
              <button
                onClick={() => setIsSpymasterView(true)}
                className={`bg-purple-500 hover:bg-purple-700 text-white font-bold py-2 px-4 rounded ${isSpymasterView ? 'border-4 border-red-500' : ''}`}
              >
                Spymaster
              </button>
            </div>
          </>
        )}
      </div>
    </main>
  );
}
