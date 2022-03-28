export interface IBoat {
  id?: number;
  name?: string;
  description?: string;
  picContentType?: string | null;
  pic?: string | null;
}

export const defaultValue: Readonly<IBoat> = {};
