class ChangeEventArgumentTypeName < ActiveRecord::Migration[5.1]
  def change

    rename_column :event_arguments, :type, :argument_type
  end
end
