class CreateAttributes < ActiveRecord::Migration[5.1]
  def change
    create_table :attributes do |t|
      t.string :attribute_name
      t.string :type
      t.references :i_class

      t.timestamps
    end
  end
end
