"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";

interface Tile {
  word: string;
  role: "RED" | "BLUE" | "NEUTRAL" | "ASSASSIN";
  revealed: boolean;
}

interface Game {
  id: string;
  board: Tile[][];
  turn: "RED" | "BLUE";
  gameOver: boolean;
  winner: "RED" | "BLUE" | null;
  version: number;
  redTilesRemaining: number;
  blueTilesRemaining: number;
}

export default function GamePage() {
  const [game, setGame] = useState<Game | null>(null);
  const [isSpymasterView, setIsSpymasterView] = useState(false);
  const [gameNotFound, setGameNotFound] = useState(false);
  const params = useParams();
  const router = useRouter();
  const gameId = params.gameId as string;

  useEffect(() => {
    let intervalId: NodeJS.Timeout | null = null;

    const fetchGame = () => {
      if (gameId) {
        fetch(`${process.env.NEXT_PUBLIC_BACKEND_URL}/api/game/${gameId}`)
          .then((response) => {
            if (response.ok) {
              return response.json();
            }
            return null;
          })
          .then((data: Game | null) => {
            if (data) {
              setGame(prevGame => {
                if (prevGame && data.version > prevGame.version) {
                  setIsSpymasterView(false);
                }
                return data;
              });
            } else {
              setGameNotFound(true);
              if (intervalId) clearInterval(intervalId);
            }
          })
          .catch((error) => {
            console.error("Error fetching game:", error);
            setGameNotFound(true);
            if (intervalId) clearInterval(intervalId);
          });
      }
    };

    fetchGame();
    intervalId = setInterval(fetchGame, 2000);

    return () => {
      if (intervalId) clearInterval(intervalId);
    };
  }, [gameId]);

  const createNewGameAndRedirect = () => {
    fetch(`${process.env.NEXT_PUBLIC_BACKEND_URL}/api/game/new`, {
      method: "POST",
    })
      .then((response) => response.json())
      .then((data: { id: string }) => {
        router.push(`/${data.id}`);
      })
      .catch((error) => console.error("Error creating new game:", error));
  };

  const resetGame = () => {
    fetch(`${process.env.NEXT_PUBLIC_BACKEND_URL}/api/game/${gameId}/new`, {
      method: "POST",
    })
      .then((response) => response.json())
      .then((data: Game) => {
        setGame(data);
        setIsSpymasterView(false);
      })
      .catch((error) => console.error("Error resetting game:", error));
  };

  const handleTileClick = (row: number, col: number) => {
    if (game?.gameOver) return;

    fetch(`${process.env.NEXT_PUBLIC_BACKEND_URL}/api/game/${gameId}/reveal`, {
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

  if (gameNotFound) {
    return (
      <main className="flex min-h-screen flex-col items-center justify-center p-24">
        <h1 className="text-4xl font-bold mb-8">Game Not Found</h1>
        <p className="mb-8">The game ID in the URL does not exist.</p>
        <button
          onClick={createNewGameAndRedirect}
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
        >
          Create a New Game
        </button>
      </main>
    );
  }

  if (!game) {
    return <div>Loading...</div>;
  }

  const turnIndicator = () => {
    if (game.gameOver) {
      return (
        <h2 className={`text-2xl font-bold ${game.winner === 'RED' ? 'text-red-500' : 'text-blue-500'}`}>
          {game.winner} Team Wins!
        </h2>
      );
    }
    return (
      <h2 className={`text-2xl font-bold ${game.turn === 'RED' ? 'text-red-500' : 'text-blue-500'}`}>
        {game.turn}&apos;s Turn
      </h2>
    );
  };

  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-8">
      <div className="flex flex-col items-center">
        <h1 className="text-4xl font-bold mb-8">Codenames</h1>
        <button
          onClick={resetGame}
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded mb-4"
        >
          New Game
        </button>
        <div className="mb-4">
          {turnIndicator()}
        </div>
        <div className="w-full max-w-4xl relative">
          <div className="absolute -top-8 left-0">
            <p className="text-2xl font-bold">
              <span className="text-red-500">{game.redTilesRemaining}</span> - <span className="text-blue-500">{game.blueTilesRemaining}</span>
            </p>
          </div>
          <div className="grid grid-cols-5 gap-4">
            {game.board.map((row, rowIndex) =>
              row.map((tile, colIndex) => (
                <div
                  key={`${rowIndex}-${colIndex}`}
                  onClick={() => !isSpymasterView && handleTileClick(rowIndex, colIndex)}
                  className={`w-40 h-28 rounded-md flex items-center justify-center text-center p-2 uppercase text-lg font-bold ${isSpymasterView || game.gameOver ? 'cursor-not-allowed' : 'cursor-pointer'} ${getTileStyling(
                    tile
                  )}`}
                >
                  {tile.word}
                </div>
              ))
            )}
          </div>
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
      </div>
    </main>
  );
}
